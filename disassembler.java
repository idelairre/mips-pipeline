class Disassembler {
  static Boolean isRFormat(int instruction) {
    return (instruction & 0xFC000000) == 0;
  }

  public static int bits5to0(int instruction) {
    return instruction & 0x3F;
  }

  public static int bits15to0(int instruction) {
    return instruction & 0xFFFF;
  }

  public static int bits15to11(int instruction) {
    return (instruction & 0xF800) >>> 11;
  }

  public static int bits20to16(int instruction) {
    return (instruction & 0x1F0000) >>> 16;
  }

  public static int bits25to21(int instruction) {
    return (instruction & 0x3E00000) >>> 21;
  }

  public static int bits31to26(int instruction) {
    return (instruction & 0xFC000000) >>> 26;
  }

  public static String opcode(int instruction) {
    return Procedure.opCodes.get((instruction & 0xFC000000) >>> 26);
  }

  public static String decode(int instruction) {
    if (isRFormat(instruction)) {
      return new RFormat(instruction).toString();
    } else {
      return new IFormat(instruction).toString();
    }
  }
}
