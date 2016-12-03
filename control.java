import java.util.HashMap;
import java.util.Map;

class Control {
  public static Map<String, Integer> signals;
  static {
    Map<String, Integer> tempMap = new HashMap<String, Integer>();
    tempMap.put("ALUOp", 0);
    tempMap.put("ALUSrc", 0); // multiplexor, 0
    tempMap.put("branch", 0);
    tempMap.put("memRead", 0);
    tempMap.put("memWrite", 0);
    tempMap.put("memToReg", 0);
    tempMap.put("regDst", 0); // multiplexor, 0 = reg destination is rt, 1 = reg destination is rd
    tempMap.put("regWrite", 0);
    signals = new HashMap<String, Integer>(tempMap);
  }

  public static int get(String control) {
    return signals.get(control);
  }

  public static void set(String control, int value) {
    signals.put(control, value);
  }

  public static Map getCodes() {
    return signals;
  }

  public static void state(String instruction) {
    if (instruction.equals("rFormat")) {
      setRFormat();
    } else if (instruction.equals("lw")) {
      setLw();
    } else if (instruction.equals("sw")) {
      setSw();
    } else {
      setBeq();
    }
  }

  private static void setRFormat() {
    signals.put("regDst", 1);
    signals.put("regWrite", 1);
    signals.put("memToReg", 0);
    signals.put("memRead", 0);
    signals.put("memWrite", 0);
    signals.put("branch", 0);
    signals.put("ALUSrc", 0);
    signals.put("ALUOp", Integer.parseInt("10", 2));
  }

  private static void setLw() {
    signals.put("regDst", 0);
    signals.put("memToReg", 1);
    signals.put("regWrite", 1);
    signals.put("memRead", 1);
    signals.put("memWrite", 0);
    signals.put("branch", 0);
    signals.put("ALUSrc", 1);
    signals.put("ALUOp", Integer.parseInt("00", 2));
  }

  private static void setSw() {
    // signals.remove("regDst");
    // signals.remove("memToReg");
    signals.put("ALUSrc", 1);
    signals.put("regWrite", 0);
    signals.put("memRead", 0);
    signals.put("memWrite", 1);
    signals.put("branch", 0);
    signals.put("ALUOp", Integer.parseInt("00", 2));
  }

  private static void setBeq() {
    // signals.remove("regDst");
    // signals.remove("memToReg");
    signals.put("ALUSrc", 0);
    signals.put("regWrite", 0);
    signals.put("memRead", 0);
    signals.put("memWrite", 0);
    signals.put("branch", 0);
  }
}
