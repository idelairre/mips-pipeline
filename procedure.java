import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

abstract class Procedure {
  public static int address = (0x7A000 - 4);
  public int rs;
  public int rt;
  public int rd;
  public int func;
  public int opcode;
  public int offset;
  public int branch;

  protected static final Map<Integer, String> opCodes;

  static {
    Map<Integer, String> tempMap = new HashMap<Integer, String>();
    tempMap.put(0x23, "lw");
    tempMap.put(0x2B, "sw");
    tempMap.put(0x04, "beq");
    tempMap.put(0x05, "bne");
    opCodes = Collections.unmodifiableMap(tempMap);
  }

  protected static final Map<Integer, String> funcCodes;

  static {
    Map<Integer, String> tempMap = new HashMap<Integer, String>();
    tempMap.put(0x20, "add");
    tempMap.put(0x22, "sub");
    tempMap.put(0x24, "and");
    tempMap.put(0x25, "or");
    tempMap.put(0x2A, "slt");
    funcCodes = Collections.unmodifiableMap(tempMap);
  }

  public Procedure(int instruction) {
    address = address + 4;
  }

  public String getOpcode() {
    return opCodes.get(opcode);
  }

  abstract public void print();

  abstract public String toString();
}
