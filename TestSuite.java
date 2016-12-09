import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSuite {
  public Pipeline pipeline = new Pipeline();

  @Test
  public void test1IFStage() {
    pipeline.run();
    assertEquals("program counter is correctly incremented and assigned to IF/ID write", Integer.toHexString(0x7A004), Integer.toHexString(IFID.write.get("incrPC")));
    assertEquals("instructions have been correctly assigned to IF/ID write", Integer.toHexString(0x00a63820), Integer.toHexString(IFID.write.get("instruction")));
  }

  @Test
  public void test2IDStage() {
    pipeline.run();

    assertEquals("program counter is correctly incremented and assigned to IF/ID write", Integer.toHexString(0x7A008), Integer.toHexString(IFID.write.get("incrPC")));
    assertEquals("instructions have been correctly assigned to IF/ID write", Integer.toHexString(0x8d0f0004), Integer.toHexString(IFID.write.get("instruction")));

    // check recent values and controls signals

    // ID/EX write
    assertEquals("program counter is correctly assigned to ID/EX register", Integer.toHexString(0x7A004), Integer.toHexString(IDEX.write.get("incrPC")));
    assertEquals("readReg1Value is correctly assigned", Integer.toHexString(0x30005), Integer.toHexString(IDEX.write.get("readReg1Value")));
    assertEquals("readReg2Value is correctly assigned", Integer.toHexString(0x30006), Integer.toHexString(IDEX.write.get("readReg2Value")));
    assertEquals("writeReg_20_16 is correctly assigned", Integer.valueOf(6), Integer.valueOf(IDEX.write.get("writeReg_20_16")));
    assertEquals("writeReg_15_11 is correctly assigned", Integer.valueOf(7), Integer.valueOf(IDEX.write.get("writeReg_15_11")));
    assertEquals("function is correctly assigned", Integer.valueOf(20).toString(), Integer.toHexString(IDEX.write.get("function")));

    // Control signals
    assertEquals("regDst is correctly assigned", Integer.valueOf(1), Integer.valueOf(IDEX.controls.get("read").get("regDst")));
    assertEquals("ALUsrc is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("ALUSrc")));
    assertEquals("ALUOp is correctly assigned", Integer.valueOf(Integer.parseInt("10", 2)), Integer.valueOf(IDEX.controls.get("read").get("ALUOp")));
    assertEquals("memRead is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("memRead")));
    assertEquals("memWrite is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("memWrite")));
    assertEquals("branch is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("branch")));
    assertEquals("memToReg is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("memToReg")));
    assertEquals("regWrite is correctly assigned", Integer.valueOf(1), Integer.valueOf(IDEX.controls.get("read").get("regWrite")));

    // IDEX.EX signals
    assertEquals("regDst is correctly assigned", Integer.valueOf(1), Integer.valueOf(IDEX.controls.get("read").get("regDst")));
    assertEquals("ALUOp is correctly assigned", Integer.valueOf(Integer.parseInt("10", 2)), Integer.valueOf(IDEX.controls.get("read").get("ALUOp")));
    assertEquals("ALUSrc is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.controls.get("read").get("ALUSrc")));

  }

  @Test
  public void test3EXStage() {
    pipeline.run();

    // check previous stage registers

    // IF/ID write
    assertEquals("program counter is correctly incremented and assigned to IF/ID write", Integer.toHexString(0x7A00C), Integer.toHexString(IFID.write.get("incrPC")));
    assertEquals("instructions have been correctly assigned to IF/ID write", Integer.toHexString(0xad09fffc), Integer.toHexString(IFID.write.get("instruction")));

    //IDEX write
    assertEquals("program counter is correctly assigned to ID/EX register", Integer.toHexString(0x7A008), Integer.toHexString(IDEX.write.get("incrPC")));
    assertEquals("readReg1Value is correctly assigned", Integer.toHexString(0x30008), Integer.toHexString(IDEX.write.get("readReg1Value")));
    assertEquals("readReg2Value is correctly assigned", Integer.toHexString(0x3000F), Integer.toHexString(IDEX.write.get("readReg2Value")));
    assertEquals("writeReg_20_16 is correctly assigned", Integer.valueOf(15), Integer.valueOf(IDEX.write.get("writeReg_20_16")));
    assertEquals("writeReg_15_11 is correctly assigned", Integer.valueOf(0), Integer.valueOf(IDEX.write.get("writeReg_15_11")));
    assertEquals("seOffassigned is correctly assigned", Integer.valueOf(0x00000004), Integer.valueOf(IDEX.write.get("seOffset")));

    // check most recent values

    // EX/MEM write
    assertEquals("SWValue is correctly assigned", Integer.toHexString(0x30006), Integer.toHexString(EXMEM.write.get("SWValue")));
    assertEquals("zero is correctly assigned", Integer.valueOf(0), Integer.valueOf(EXMEM.write.get("zero")));
    assertEquals("ALUResult is correctly assigned", Integer.toHexString(0x6000B), Integer.toHexString(EXMEM.write.get("ALUResult")));
    assertEquals("writeRegNum is correctly assigned", Integer.valueOf(7), Integer.valueOf(EXMEM.write.get("writeRegNum")));

    // controls signals
    assertEquals("memRead is correctly assigned to EXMEM controls signals", Integer.valueOf(0), Integer.valueOf(EXMEM.controls.get("read").get("memRead")));
    assertEquals("memWrite is correctly assigned to EXMEM controls signals", Integer.valueOf(0), Integer.valueOf(EXMEM.controls.get("read").get("memWrite")));
    assertEquals("branch is correctly assigned to EXMEM controls signals", Integer.valueOf(0), Integer.valueOf(EXMEM.controls.get("read").get("branch")));
    assertEquals("memToReg is correctly assigned to EXMEM controls signals", Integer.valueOf(0), Integer.valueOf(EXMEM.controls.get("read").get("memToReg")));
    assertEquals("regWrite is correctly assigned to EXMEM controls signals", Integer.valueOf(1), Integer.valueOf(EXMEM.controls.get("read").get("regWrite")));
  }

  @Test
  public void test4MEMStage() {
    pipeline.run();

    // check previous values

    // IF/ID write
    assertEquals("program counter is correctly incremented and assigned to IF/ID write", Integer.toHexString(0x7A010), Integer.toHexString(IFID.write.get("incrPC")));
    assertEquals("instructions have been correctly assigned to IF/ID write", Integer.toHexString(0x00625022), Integer.toHexString(IFID.write.get("instruction")));

    // current values

    // MEM/WB write
    assertEquals("ALUResult is correctly assigned", Integer.toHexString(0x6000B), Integer.toHexString(MEMWB.write.get("ALUResult")));
    assertEquals("writeRegNum is correctly assigned", Integer.valueOf(7), Integer.valueOf(MEMWB.write.get("writeRegNum")));

    // controls signals
    assertEquals("memToReg is correctly assigned", Integer.valueOf(0), Integer.valueOf(MEMWB.controls.get("read").get("memToReg")));
    assertEquals("regWrite is correctly assigned", Integer.valueOf(1), Integer.valueOf(MEMWB.controls.get("read").get("regWrite")));
  }

  @Test
  public void test5WBStage() {
    pipeline.run();

    assertEquals("ALUResult is correctly assigned", Integer.toHexString(0x6000B), Integer.toHexString(Registers.writeData));
  }
}
