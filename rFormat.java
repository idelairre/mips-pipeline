class RFormat extends Procedure {
  public RFormat(int hex) {
    super(hex);
    rs = setRs(hex);
    rt = setRt(hex);
    rd = setRd(hex);
    func = setFunct(hex);
  }

  private static int setRs(int hex) {
    return (hex & 0x3E00000) >>> 21;
  }

  private static int setRt(int hex) {
    return (hex & 0x1F0000) >>> 16;
  }

  private static int setRd(int hex) {
    return (hex & 0xF800) >>> 11;
  }

  private static int setFunct(int hex) {
    return hex & 0x3F;
  }

  public String toString() {
    String currentAddress = Integer.toHexString(address);
    String procedure = funcCodes.get(func) + " $" + rd + ", $" + rs + ", $" + rt;
    return currentAddress + " " + procedure;
  }

  public void print() {
    System.out.println(toString());
  }
}
