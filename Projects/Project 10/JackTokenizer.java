import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JackTokenizer {

    public final static int KEYWORD = 1;
    public final static int SYMBOL = 2;
    public final static int IDENTIFIER = 3;
    public final static int INT_CONST = 4;
    public final static int STRING_CONST = 5;
    public final static int CLASS = 6;
    public final static int METHOD = 7;
    public final static int FUNCTION = 8;
    public final static int CONSTRUCTOR = 9;
    public final static int INT = 10;
    public final static int BOOLEAN = 11;
    public final static int CHAR = 12;
    public final static int VOID = 13;
    public final static int VAR = 14;
    public final static int STATIC = 15;
    public final static int FIELD = 16;
    public final static int LET = 17;
    public final static int DO = 18;
    public final static int IF = 19;
    public final static int ELSE = 20;
    public final static int WHILE = 21;
    public final static int RETURN = 22;
    public final static int TRUE = 23;
    public final static int FALSE = 24;
    public final static int NULL = 25;
    public final static int THIS = 26;

    private Scanner scanner;
    private String currentToken;
    private int currentTokenType;
    private int iterator;
    private ArrayList<String> tokens;

    private static Pattern tokenPatterns;
    private static String keyWordRegex;
    private static String symbolRegex;
    private static String intRegex;
    private static String stringRegex;
    private static String idRegex;

    private static HashSet<Character> simpleOperands = new HashSet<Character>();
    private static HashMap<String,Integer> keywords = new HashMap<String, Integer>();


    static {

        simpleOperands.add('+');
        simpleOperands.add('-');
        simpleOperands.add('*');
        simpleOperands.add('/');
        simpleOperands.add('&');
        simpleOperands.add('|');
        simpleOperands.add('<');
        simpleOperands.add('>');
        simpleOperands.add('=');

        keywords.put("var",VAR);
        keywords.put("int",INT);
        keywords.put("char",CHAR);
        keywords.put("boolean",BOOLEAN);
        keywords.put("void",VOID);
        keywords.put("true",TRUE);
        keywords.put("false",FALSE);
        keywords.put("null",NULL);
        keywords.put("this",THIS);
        keywords.put("let",LET);
        keywords.put("do",DO);
        keywords.put("if",IF);
        keywords.put("else",ELSE);
        keywords.put("while",WHILE);
        keywords.put("return",RETURN);
        keywords.put("class",CLASS);
        keywords.put("constructor",CONSTRUCTOR);
        keywords.put("function",FUNCTION);
        keywords.put("method",METHOD);
        keywords.put("field",FIELD);
        keywords.put("static",STATIC);



}


    public JackTokenizer(File inFile) {

        try {

            scanner = new Scanner(inFile);
            String preprocessed = "";
            String line = "";

            while(scanner.hasNext()){

                line = deleteComments(scanner.nextLine()).trim();

                if (line.length() > 0)
                    preprocessed += line + "\n";

            }

            preprocessed = deleteCommentBlock(preprocessed).trim();

            createRegex();

            Matcher m = tokenPatterns.matcher(preprocessed);
            tokens = new ArrayList<String>();
            iterator = 0;

            while (m.find()){

                tokens.add(m.group());

            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

        currentToken = "";
        currentTokenType = -1;

    }


    private void createRegex(){

        keyWordRegex = "";

        for (String s: keywords.keySet())
            keyWordRegex = keyWordRegex + s + "|";

        symbolRegex = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
        intRegex = "[0-9]+";
        stringRegex = "\"[^\"\n]*\"";
        idRegex = "[\\w_]+";

        tokenPatterns = Pattern.compile(keyWordRegex + symbolRegex + "|" + intRegex + "|" + stringRegex + "|" + idRegex);
    }



    public boolean hasMoreTokens() { return iterator < tokens.size(); }

    public void advance(){

        if (hasMoreTokens()) {
            currentToken = tokens.get(iterator);
            iterator++;
        }

        if (currentToken.matches(keyWordRegex)){
            currentTokenType = KEYWORD;
        }else if (currentToken.matches(symbolRegex)){
            currentTokenType = SYMBOL;
        }else if (currentToken.matches(intRegex)){
            currentTokenType = INT_CONST;
        }else if (currentToken.matches(stringRegex)){
            currentTokenType = STRING_CONST;
        }else if (currentToken.matches(idRegex)){
            currentTokenType = IDENTIFIER;
        }

    }

    public String getCurrentToken() { return currentToken; }

    public int tokenType(){ return currentTokenType; }

    public int keyWord(){ return keywords.get(currentToken); }

    public char symbol(){ return currentToken.charAt(0); }

    public String identifier(){ return currentToken; }

    public int intVal(){ return Integer.parseInt(currentToken); }

    public String stringVal(){ return currentToken.substring(1, currentToken.length() - 1); }


    public void itDown(){

        if (iterator > 0)
            iterator--;
    }

    public boolean isOperand(){ return simpleOperands.contains(symbol()); }


    public static String deleteComments(String line){

        int index = line.indexOf("//");

        if (index != -1)
            line = line.substring(0, index);

        return line;
    }


    public static String deleteCommentBlock(String line){

        int start = line.indexOf("/*");

        if (start == -1)
            return line;

        String result = line;

        int end = line.indexOf("*/");

        while(start != -1){

            if (end == -1)
                return line.substring(0,start - 1);

            result = result.substring(0,start) + result.substring(end + 2);
            start = result.indexOf("/*");
            end = result.indexOf("*/");
        }

        return result;
    }
}