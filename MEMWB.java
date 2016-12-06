import java.util.HashMap;
import java.util.Map;

class MEMWB {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;
  public static Map<String, Integer> WB = new HashMap<String, Integer>();
  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("LWDataValue", 0);
    tempMap.put("ALUResult", 0);
    tempMap.put("writeRegNum", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }
  private static Map<String, Integer> Controls;

  public static Map<String, Integer> controls() {
    Controls = new HashMap<String, Integer>();
    Controls.putAll(WB);
    return Controls;
  }
}
