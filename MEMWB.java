import java.util.HashMap;
import java.util.Map;

class MEMWB {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;

  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("LWDataValue", 0);
    tempMap.put("ALUResult", 0);
    tempMap.put("writeRegNum", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }
}
