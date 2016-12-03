class IFormat extends Procedure {
  // I-Format procedures are called [opcode], [rt], [rs], [offset], they are REPRESENTED as [opcode], [rs], [rt], [offset]
  public IFormat(int hex) {
    super(hex);
    opcode = setOpcode(hex);
    rs = setRs(hex);
    rt = setRt(hex);
    offset = setOffset(hex);
  }

  private static int setRt(int hex) {
    return (hex & 0x1F0000) >>> 16;
  }

  private static int setRs(int hex) {
    return (hex & 0x3E00000) >>> 21;
  }

  public int getBranch(short offset) {
    return (address + 4) + (offset << 2);
  }

  private int setOffset(int hex) {
    short offset = (short) (hex & 0xFFFF);
    if (opCodes.get(opcode).equals("beq") || opCodes.get(opcode).equals("bne")) {
      return getBranch(offset);
    }
    return (int) offset;
  }

  static int setOpcode(int hex) {
    return (hex & 0xFC000000) >>> 26;
  }

  public String toString() {
    String procedure;
    String currentAddress = Integer.toHexString(address);
    if (opCodes.get(opcode).equals("beq") || opCodes.get(opcode).equals("bne")) {
      procedure = opCodes.get(opcode) + " $" + rs + ", $" + rt + ", address " + Integer.toHexString(offset);
    } else {
      procedure = opCodes.get(opcode) + " $" + rt + ", " + String.valueOf(offset) + "($" + rs + ')';
    }
    return currentAddress + " " + procedure;
  }

  public void print() {
    System.out.println(toString());
  }

}
