import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.lang.Thread;
import java.lang.Runnable;

class Global {
  public static int pc = 0x7A000;
  public static int[] Main_Memory = new int[1024];
  public static int[] Regs = new int[32];
  public static final Map<Integer, Integer> instructions;

  public static boolean test = false;

  static {
    Map<Integer, Integer> tempMap = new LinkedHashMap<Integer, Integer>();
    if (test) {
      tempMap.put(0x7A000, 0x00a63820);
      tempMap.put(0x7A004, 0x8d0f0004);
      tempMap.put(0x7A008, 0xad09fffc);
      tempMap.put(0x7A00C, 0x00625022);
      tempMap.put(0x7A010, 0x10c8fffb);
    } else {
      tempMap.put(0x7A000, 0xa1020000);
      tempMap.put(0x7A004, 0x810AFFFC);
      tempMap.put(0x7A008, 0x00831820);
      tempMap.put(0x7A00C, 0x01263820);
      tempMap.put(0x7A010, 0x01224820);
      tempMap.put(0x7A014, 0x81180000);
      tempMap.put(0x7A018, 0x81510010);
      tempMap.put(0x7A01C, 0x00624022);
      tempMap.put(0x7A020, 0x00000000);
      tempMap.put(0x7A024, 0x00000000);
      tempMap.put(0x7A028, 0x00000000);
      tempMap.put(0x7A02C, 0x00000000);
    }
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
        if (i == 0) {
          Regs[i] = 0;
        } else {
          Regs[i] = 0x100 + i;
        }
      }
    }
  }
}

class Pipeline {
  private int signExtend;

  public Pipeline IF_stage() {
    IFID.write.put("instruction", Global.instructions.get(Global.pc));
    IFID.write.put("incrPC", Global.pc + 4);
    Global.pc += 4;
    return this;
  }

  public Pipeline ID_stage() {
    int instruction = IFID.read.get("instruction");

    IDEX.write.put("incrPC", IFID.read.get("incrPC"));
    IDEX.write.put("function", Disassembler.bits5to0(instruction));

    if (instruction == 0) { // if its a noop
      Control.state("noop");
    } else if (Disassembler.bits31to26(instruction) == 0) { // if its R-Type
      Control.state("rFormat");
    } else {
      Control.state(Disassembler.opcode(instruction));
    }

    Registers.readReg1 = Disassembler.bits25to21(instruction);
    Registers.readReg2 = Disassembler.bits20to16(instruction);

    signExtend = (int) new Integer(Disassembler.bits15to0(instruction)).shortValue();

    IDEX.write.put("seOffset", signExtend);

    IDEX.write.put("readReg1Value", Global.Regs[Registers.readReg1]);
    IDEX.write.put("readReg2Value", Global.Regs[Registers.readReg2]);
    IDEX.write.put("writeReg_20_16", Disassembler.bits20to16(instruction));
    IDEX.write.put("writeReg_15_11", Disassembler.bits15to11(instruction));

    // control signals
    IDEX.controls.put("write", new HashMap<String, Integer>() {{
      put("regDst", Control.get("regDst"));
      put("ALUSrc", Control.get("ALUSrc"));
      put("ALUOp", Control.get("ALUOp"));
      put("memRead", Control.get("memRead"));
      put("memWrite", Control.get("memWrite"));
      put("branch", Control.get("branch"));
      put("memToReg", Control.get("memToReg"));
      put("regWrite", Control.get("regWrite"));
    }});

    return this;
  }

  public Pipeline EX_stage() {
    // the signals to be set are RegDst, ALUOP, and ALUSrc.
    // The signals select the Result register, the ALU operation
    // and either Read data 2 or a sign-extended immediate
    // for the ALU

    ALU.bits5to0 = IDEX.read.get("function");
    ALU.bits15to0 = IDEX.read.get("seOffset");

    int operand1 = IDEX.read.get("readReg1Value");
    int operand2;

    if (IDEX.controls.get("read").get("ALUSrc") == 0) {
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

    EXMEM.write.put("SWValue", IDEX.read.get("readReg2Value"));

    if (IDEX.controls.get("read").get("regDst") == 0) {
      EXMEM.write.put("writeRegNum", IDEX.read.get("writeReg_20_16"));
    } else {
      EXMEM.write.put("writeRegNum", IDEX.read.get("writeReg_15_11"));
    }

    EXMEM.controls.put("write", new HashMap<String, Integer>() {{
      put("memRead", IDEX.controls.get("read").get("memRead"));
      put("memWrite", IDEX.controls.get("read").get("memWrite"));
      put("branch", IDEX.controls.get("read").get("branch"));
      put("memToReg", IDEX.controls.get("read").get("memToReg"));
      put("regWrite", IDEX.controls.get("read").get("regWrite"));
    }});

    return this;
  }

  public Pipeline MEM_stage() {
    MEMWB.write.put("writeRegNum", EXMEM.read.get("writeRegNum"));
    MEMWB.write.put("ALUResult", EXMEM.read.get("ALUResult"));

    if (EXMEM.controls.get("read").get("memRead") == 1) {// if its a load than set the lw data value
      if (Global.test) {
        System.out.println("LWDataValue = mem contents @ " + Integer.toHexString(EXMEM.read.get("ALUResult")));
      } else {
        MEMWB.write.put("LWDataValue", Global.Main_Memory[EXMEM.read.get("ALUResult")]);
      }
    }

    if (EXMEM.controls.get("read").get("memWrite") == 1) {
      if (Global.test) {
        System.out.println("Value " + Integer.toHexString(EXMEM.read.get("SWValue")) + " written to memory address " + Integer.toHexString(EXMEM.read.get("ALUResult")));
      } else {
        Global.Main_Memory[EXMEM.read.get("ALUResult")] = EXMEM.read.get("SWValue");
      }
    }

    MEMWB.controls.put("write", new HashMap<String, Integer>() {{
      put("memToReg", EXMEM.controls.get("read").get("memToReg"));
      put("regWrite", EXMEM.controls.get("read").get("regWrite"));
    }});

    return this;
  }

  public Pipeline WB_stage() {
    // places the ALU result back into the register file in the middle of the datapath
    // OR, read the data from the mem/wb pipeline register and writing it into the register file

    if (MEMWB.controls.get("read").get("regWrite") == 1) {
      if (MEMWB.controls.get("read").get("memToReg") == 0) {
        // the value fed to the register Write data input
        // comes from the ALU
        if (Global.test) {
          System.out.println("Writing " + Integer.toHexString(MEMWB.read.get("ALUResult")) + " to register " + MEMWB.read.get("writeRegNum"));
        }
        Registers.writeData = MEMWB.read.get("ALUResult");
      } else {
        // the value fed to the register Write data input
        // comes from the data memory
        if (Global.test) {
          System.out.println("Writing whatever is stored at " + Integer.toHexString(MEMWB.read.get("ALUResult")) + " to register " + MEMWB.read.get("writeRegNum"));
        } else {
          Registers.writeData = Global.Main_Memory[MEMWB.read.get("ALUResult")];
        }
      }
      // the register on the write register input is written with the value on the write data input
      if (!Global.test) {
        Global.Regs[MEMWB.read.get("writeRegNum")] = Registers.writeData;
      }
    }

    return this;
  }

  public void Copy_write_to_read() {
    IFID.read.putAll(IFID.write);
    IDEX.read.putAll(IDEX.write);
    IDEX.controls.put("read", IDEX.controls.get("write"));
    EXMEM.read.putAll(EXMEM.write);
    EXMEM.controls.put("read", EXMEM.controls.get("write"));
    MEMWB.read.putAll(MEMWB.write);
    MEMWB.controls.put("read", MEMWB.controls.get("write"));
  }

  public void run() {
    IF_stage().ID_stage().EX_stage().MEM_stage().WB_stage().Print_out_everything().Copy_write_to_read();
  }

  public Pipeline Print_out_everything() {
    // System.out.println("\n" + Disassembler.decode(Global.instructions.get(Global.pc)));
    System.out.println("\nIF/ID Write");
    System.out.println(RegisterService.toString(IFID.write));
    System.out.println("IF/ID Read");
    System.out.println(RegisterService.toString(IFID.read));
    System.out.println("ID/EX Write");
    System.out.println("Controls: " + IDEX.controls.get("write"));
    System.out.println(RegisterService.toString(IDEX.write));
    System.out.println("ID/EX Read");
    System.out.println("Controls: " + IDEX.controls.get("read"));
    System.out.println(RegisterService.toString(IDEX.read));
    System.out.println("Controls: " + IDEX.controls.get("write"));
    System.out.println("EX/MEM Write");
    System.out.println(RegisterService.toString(EXMEM.write));
    System.out.println("EX/MEM Read");
    System.out.println("Controls: " + EXMEM.controls.get("read"));
    System.out.println(RegisterService.toString(EXMEM.read));
    System.out.println("MEM/WB Write");
    System.out.println("Controls: " + MEMWB.controls.get("write"));
    System.out.println(RegisterService.toString(MEMWB.write));
    System.out.println("MEM/WB Read");
    System.out.println("Controls: " + MEMWB.controls.get("read"));
    System.out.println(RegisterService.toString(MEMWB.read));

    System.out.println(Arrays.toString(Global.Regs));
    System.out.println(Arrays.toString(Global.Main_Memory));

    return this;
  }

  public static void main(String[] args) {
    Pipeline pipeline = new Pipeline();
    while (Global.instructions.get(Global.pc) != null) {
      pipeline.run();
    }
  }
}
