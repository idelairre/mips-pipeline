import java.util.HashMap;
import java.util.Map;

class IFID {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;

  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("instruction", 0);
    tempMap.put("incrPC", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }
}
