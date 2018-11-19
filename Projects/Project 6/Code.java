//package HackAssembler;

import java.util.HashMap;
import java.util.Map;

public class Code {

    private static Map <String, String> compStrToBinary = new HashMap<>();

    static{
        compStrToBinary.put("0","0101010");
        compStrToBinary.put("1","0111111");
        compStrToBinary.put("-1","0111010");
        compStrToBinary.put("D","0001100");
        compStrToBinary.put("A","0110000");
        compStrToBinary.put("!D","0001101");
        compStrToBinary.put("!A","0110001");
        compStrToBinary.put("-D","0001111");
        compStrToBinary.put("-A","0110011");
        compStrToBinary.put("D+1","0011111");
        compStrToBinary.put("A+1","0110111");
        compStrToBinary.put("D-1","0001110");
        compStrToBinary.put("A-1","0110010");
        compStrToBinary.put("D+A","0000010");
        compStrToBinary.put("D-A","0010011");
        compStrToBinary.put("A-D","0000111");
        compStrToBinary.put("D&A","0000000");
        compStrToBinary.put("D|A","0010101");
        compStrToBinary.put("M","1110000");
        compStrToBinary.put("!M","1110001");
        compStrToBinary.put("-M","1110011");
        compStrToBinary.put("M+1","1110111");
        compStrToBinary.put("M-1","1110010");
        compStrToBinary.put("D+M","1000010");
        compStrToBinary.put("D-M","1010011");
        compStrToBinary.put("M-D","1000111");
        compStrToBinary.put("D&M","1000000");
        compStrToBinary.put("D|M","1010101");
    }

    public static String dest(String mnemonic){
        String d1 = "0", d2 = "0", d3 = "0", code = "";
        if (!mnemonic.contains("J")) {
            if (mnemonic.contains("M"))
                d3 = "1";
            if (mnemonic.contains("D"))
                d2 = "1";
            if (mnemonic.contains("A"))
                d1 = "1";
        }
        code = code + d1 + d2 + d3;
        return code;
    }

    public static String jump (String mnemonic){
        switch (mnemonic){
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            case "JMP":
                return "111";
            default:
                return "000";
        }
    }

    public static String comp (String mnemonic){
        return compStrToBinary.get(mnemonic);
    }
}
