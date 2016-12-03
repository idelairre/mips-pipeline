import java.util.HashMap;
import java.util.Map;

class IDEX {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;

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
}
