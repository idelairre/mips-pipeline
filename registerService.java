import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class RegisterService {
  static String toString(Map<String, Integer> map) {
    StringBuilder sb = new StringBuilder();
    Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
    sb.append('{');
    while (iter.hasNext()) {
      Entry<String, Integer> entry = iter.next();
      sb.append(entry.getKey());
      sb.append('=');
      if (entry.getKey().equals("incrPC") ||
      entry.getKey().equals("function") ||
      entry.getKey().equals("readReg1Value") ||
      entry.getKey().equals("readReg2Value") ||
      entry.getKey().equals("instruction") ||
      entry.getKey().equals("SWValue") ||
      entry.getKey().equals("seOffset") ||
      entry.getKey().equals("ALUResult")) {
        sb.append(Integer.toHexString(entry.getValue()));
      } else {
        sb.append(entry.getValue());
      }
      if (iter.hasNext()) {
        sb.append(", ");
      } else {
        sb.append('}');
      }
    }
    return sb.toString();
  }
}
