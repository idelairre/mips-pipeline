import java.util.HashMap;
import java.util.Map;

class EXMEM {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;
  public static Map<String, Map<String, Integer>> controls = new HashMap<String, Map<String, Integer>>() {{
    put("write", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
      put("memRead", 0);
      put("memWrite", 0);
      put("branch", 0);
    }});
    put("read", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
      put("memRead", 0);
      put("memWrite", 0);
      put("branch", 0);
    }});
  }};
  private static Map<String, Integer> Controls;

  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("calcBTA", 0);
    tempMap.put("writeRegNum", 0);
    tempMap.put("zero", 0);
    tempMap.put("ALUResult", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }

  public static Map<String, Integer> controls() {
    Controls = new HashMap<String, Integer>();
    // Controls.putAll(WB);
    // Controls.putAll(M);
    return Controls;
  }
}
