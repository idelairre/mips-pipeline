import java.util.HashMap;
import java.util.Map;

class IDEX {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;
  public static Map<String, Integer> WB = new HashMap<String, Integer>();
  public static Map<String, Integer> M = new HashMap<String, Integer>();
  public static Map<String, Integer> EX = new HashMap<String, Integer>();
  public static Object lock = new Object();
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
    Controls.putAll(WB);
    Controls.putAll(M);
    Controls.putAll(EX);
    return Controls;
  }
}
