import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.lang.Thread;
import java.lang.Runnable;

class Global {
  public static int pc = 0x7A000;
  public static int[] Main_Memory = new int[1024];
  public static int[] Regs = new int[32];
  public static final Map<Integer, Integer> instructions;

  public static boolean test = true;

  static {
    Map<Integer, Integer> tempMap = new LinkedHashMap<Integer, Integer>();
    tempMap.put(0x7A000, 0x00a63820);
    tempMap.put(0x7A004, 0x8d0f0004);
    tempMap.put(0x7A008, 0xad09fffc);
    tempMap.put(0x7A00C, 0x00625022);
    tempMap.put(0x7A010, 0x10c8fffb);
    instructions = Collections.unmodifiableMap(tempMap);
  }

  static {
    for (int i = 0, j = 0; i < Main_Memory.length; i++, j++) {
      if (j == (0xFF + 1)) {
        j = 0;
      }
      Main_Memory[i] = j;
    }
  }

  static {
    for (int i = 0; i < Regs.length; i++) {
      if (test) {
        Regs[i] = 0x30000 + i;
      } else {
        Regs[i] = 0x100 + i;
      }
    }
  }
}

class Pipeline implements Runnable {
  private int signExtend;
  public boolean wait = false;

  public boolean processing() {
    return wait;
  }

  public Pipeline IF_stage() {
    synchronized(IFID.write) {
      IFID.write.put("instruction", Global.instructions.get(Global.pc));
      IFID.write.put("incrPC", Global.pc + 4);
      Global.pc += 4;
      return this;
    }
  }

  public Pipeline ID_stage() {
    // sets no optional control lines
    synchronized(IFID.read) {
      IFID.read.putAll(IFID.write);

      int instruction = IFID.read.get("instruction");

      IDEX.write.put("incrPC", IFID.read.get("incrPC"));

      Registers.readReg1 = Disassembler.bits25to21(instruction);
      Registers.readReg2 = Disassembler.bits20to16(instruction);

      signExtend = (int) new Integer(Disassembler.bits15to0(instruction)).shortValue();

      IDEX.write.put("seOffset", signExtend);

      IDEX.write.put("readReg1Value", Global.Regs[Registers.readReg1]);
      IDEX.write.put("readReg2Value", Global.Regs[Registers.readReg2]);
      IDEX.write.put("writeReg_20_16", Disassembler.bits20to16(instruction));
      IDEX.write.put("writeReg_15_11", Disassembler.bits15to11(instruction));

      if (Disassembler.bits31to26(instruction) == 0) { // if its R-Type
        Control.state("rFormat");
        IDEX.write.put("function", Disassembler.bits5to0(instruction));
        // IDEX.write.remove("seOffset"); // might need a better way to represent "doesn't matter"
      } else {
        Control.state(Disassembler.opcode(instruction));
      }

      if (Control.get("regDst") == 1) { // Mux1
        Registers.writeReg = Disassembler.bits15to11(instruction);
      }
      return this;
    }
  }

  public Pipeline EX_stage() {
    // the signals to be set are RegDst, ALUOP, and ALUSrc.
    // The signals select the Result register, the ALU operation
    // and either Read data 2 or a sign-extended immediate
    // for the ALU

    synchronized(IDEX.write) {
      IDEX.read.putAll(IDEX.write);

      ALU.bits5to0 = IDEX.read.get("function");
      ALU.bits15to0 = IDEX.read.get("seOffset");

      int operand1 = IDEX.read.get("readReg1Value");
      int operand2;

      if (Control.get("ALUSrc") == 0) {
        operand2 = IDEX.read.get("readReg2Value");
      } else {
        operand2 = IDEX.read.get("seOffset");
      }

      int ALUResult = ALU.run(operand1, operand2);

      EXMEM.write.put("ALUResult", ALUResult);

      if (ALUResult == 0) {
        EXMEM.write.put("zero", 1);
      } else {
        EXMEM.write.put("zero", 0);
      }

      if (Control.get("memToReg") == 0) {
        // the value fed to the register Write data input
        // comes from the ALU
      } else {
        // the value fed to the register Write data input
        // comes from the data memory

      }

      EXMEM.write.put("SWValue", IDEX.read.get("readReg2Value"));

      if (Control.get("regDst") == 0) {
        EXMEM.write.put("writeRegNum", IDEX.read.get("writeReg_20_16"));
      } else {
        EXMEM.write.put("writeRegNum", IDEX.read.get("writeReg_15_11"));
      }
      return this;
    }
  }

  public Pipeline MEM_stage() {
    synchronized(EXMEM.write) {
      EXMEM.read.putAll(EXMEM.write);

      // MEMWB.write.put("LWDataValue", 0); // doesn't matter
      MEMWB.write.put("writeRegNum", EXMEM.read.get("writeRegNum"));
      MEMWB.write.put("ALUResult", EXMEM.read.get("ALUResult"));
      return this;
    }
  }

  public Pipeline WB_Stage() {
    synchronized(MEMWB.write) {
      MEMWB.read.putAll(MEMWB.write);
      Global.Main_Memory[EXMEM.write.get("writeRegNum")] = MEMWB.read.get("ALUResult");
      return this;
    }
  }

  public void run() {
    IF_stage().printEverything().ID_stage().printEverything().EX_stage().printEverything().MEM_stage().printEverything().WB_Stage().printEverything();
  }

  public Pipeline printEverything() {
    synchronized(IFID.read) {
      wait = true;
      // System.out.println("\n" + Disassembler.decode(IFID.write.get("instruction")));
      System.out.println("\nIF/ID Write: " + RegisterService.toString(IFID.write));
      System.out.println("IF/ID Read: " + RegisterService.toString(IFID.read));
      System.out.println("ID/EX Write: " + RegisterService.toString(IDEX.write));
      System.out.println("ID/EX Read: " + RegisterService.toString(IDEX.read));
      System.out.println("EX/MEM Write: " + RegisterService.toString(EXMEM.write));
      System.out.println("EX/MEM Read: " + RegisterService.toString(EXMEM.read));
      System.out.println("MEM/WB Write: " + RegisterService.toString(MEMWB.write));
      System.out.println("MEM/WB Read: " + RegisterService.toString(MEMWB.read));
      System.out.println("Controls: " + Control.signals);
      return this;
    }
  }

  public static void copyWriteToRead() {

  }

  public static void main(String[] args) {
    Iterator<Entry<Integer, Integer>> it = Global.instructions.entrySet().iterator();
    int cycle = 0;
    while(it.hasNext()) {
      cycle++;
      Entry<Integer, Integer> entry = it.next();
      Pipeline pipeline = new Pipeline();
      Thread thread = new Thread(pipeline);
      thread.start();
      System.out.println("Cycle: " + cycle);
      while (!pipeline.processing()) { }
    }
  }
}
