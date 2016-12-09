import java.util.HashMap;
import java.util.Map;

class IDEX {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;
  public static Map<String, Map<String, Integer>> controls = new HashMap<String, Map<String, Integer>>() {{
    put("write", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
      put("memRead", 0);
      put("memWrite", 0);
      put("branch", 0);
      put("regDst", 0);
      put("ALUSrc", 0);
      put("ALUOp", 0);
    }});
    put("read", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
      put("memRead", 0);
      put("memWrite", 0);
      put("branch", 0);
      put("regDst", 0);
      put("ALUSrc", 0);
      put("ALUOp", 0);
    }});
  }};
  private static Map<String, Integer> Controls;

  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("incrPC", 0);
    tempMap.put("readReg1Value", 0);
    tempMap.put("readReg2Value", 0);
    tempMap.put("seOffset", 0);
    tempMap.put("writeReg_20_16", 0);
    tempMap.put("writeReg_15_11", 0);
    tempMap.put("function", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }

  public static Map<String, Integer> controls() {
    Controls = new HashMap<String, Integer>();
    return Controls;
  }
}
