import java.util.HashMap;
import java.util.Map;

class EXMEM {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;

  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("calcBTA", 0);
    tempMap.put("writeRegNum", 0);
    tempMap.put("zero", 0);
    tempMap.put("ALUResult", 0);
    tempMap.put("SWValue", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }
}
