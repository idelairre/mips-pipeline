import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ALU {
  public static int bits15to0;
  public static int bits5to0;

  public static int run(int operand1, int operand2) {
    // if ALUSrc = 0, than the second ALU operand comes from the
    // second register file output
    // if ALUSrc = 1 than the second ALU operand comes from the sign
    // extended 16 bit immediate field
    int result = 0;

    if (IDEX.controls.get("read").get("ALUOp") == 0) { // add
      result = operand1 + operand2;
    } else if (IDEX.controls.get("read").get("ALUOp") == 2) { // ALUOp == 10
      result = performRType(operand1, operand2);
    }
    return result;
  }

  public static int performRType(int operand1, int operand2) {
    int result = 0;
    if (bits5to0 == Integer.parseInt("100000", 2)) {
      result = operand1 + operand2;
    } else if (bits5to0 == Integer.parseInt("100010", 2)) {
      result = operand1 - operand2;
    } else if (bits5to0 == Integer.parseInt("100100", 2)) {
      result = operand1 & operand2;
    } else if (bits5to0 == Integer.parseInt("100101", 2)) {
      result = operand1 | operand2;
    } else if (bits5to0 == Integer.parseInt("101010", 2)) {
      // set on less than
      if (operand1 < operand2) {
        result = 1;
      } else if (operand1 >= operand2) {
        result = 0;
      }
    }
    return result;
  }
}
