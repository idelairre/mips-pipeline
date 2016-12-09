import java.util.HashMap;
import java.util.Map;

class MEMWB {
  public static Map<String, Integer> write;
  public static Map<String, Integer> read;
  public static Map<String, Map<String, Integer>> controls = new HashMap<String, Map<String, Integer>>() {{
    put("write", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
    }});
    put("read", new HashMap<String, Integer>() {{
      put("memToReg", 0);
      put("regWrite", 0);
    }});
  }};
  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("LWDataValue", 0);
    tempMap.put("ALUResult", 0);
    tempMap.put("writeRegNum", 0);
    read = new HashMap<String, Integer>(tempMap);
    write = new HashMap<String, Integer>(tempMap);
  }
}
