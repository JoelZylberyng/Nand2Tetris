import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Parser {
    private Scanner scanner;
    private String currentCommand;
    public static final int ARITHMETIC = 0;
    public static final int PUSH = 1;
    public static final int POP = 2;
    public static final int LABEL = 3;
    public static final int GOTO = 4;
    public static final int IF = 5;
    public static final int FUNCTION = 6;
    public static final int RETURN = 7;
    public static final int CALL = 8;
    public static final ArrayList<String> arithmeticCommands = new ArrayList<String>();
    private int argType;
    private String argument1;
    private int argument2;

    static {

        arithmeticCommands.add("add");
        arithmeticCommands.add("sub");
        arithmeticCommands.add("neg");
        arithmeticCommands.add("eq");
        arithmeticCommands.add("gt");
        arithmeticCommands.add("lt");
        arithmeticCommands.add("and");
        arithmeticCommands.add("or");
        arithmeticCommands.add("not");

    }


    /**
     * Constructor for Parser. Cleans comments in file.
     * @param fileIn the file to read from
     */
    public Parser(File fileIn) {

        argType = -1;
        argument1 = "";
        argument2 = -1;

        try {
            scanner = new Scanner(fileIn);

            String preProcessed = "";
            String line;

            while(scanner.hasNext()){

                line = noComments(scanner.nextLine()).trim();

                if (line.length() > 0)
                    preProcessed += line + "\n";

            }

            scanner = new Scanner(preProcessed.trim());

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }


    /**
     * checks whether more commands exist in file.
     * @return true if more commands are available
     */
    public boolean hasMoreCommands(){return scanner.hasNextLine();}


    /**
     * Reads next command from file and makes it current command
     * Called only when hasMoreCommands() returns true
     */
    public void advance(){

        currentCommand = scanner.nextLine();
        argument1 = "";
        argument2 = -1;

        String[] segments = currentCommand.split(" ");

        if (segments.length > 3)
            throw new IllegalArgumentException("Invalid number of arguments");

        if (arithmeticCommands.contains(segments[0])){

            argType = ARITHMETIC;
            argument1 = segments[0];

        }else if (segments[0].equals("return")) {

            argType = RETURN;
            argument1 = segments[0];

        }else {

            argument1 = segments[1];
            String tempSegs = segments[0];

            switch (tempSegs){
                case "push":
                    argType = PUSH;
                    break;
                case "pop":
                    argType = POP;
                    break;
                case "label":
                    argType = LABEL;
                    break;
                case "if-goto":
                    argType = IF;
                    break;
                case "goto":
                    argType = GOTO;
                    break;
                case "function":
                    argType = FUNCTION;
                    break;
                case "call":
                    argType = CALL;
                    break;
                default:
                    argType = -1;
            }

            if (argType == -1)
                throw new IllegalArgumentException("Unknown command type");

            if (argType == PUSH || argType == POP || argType == FUNCTION || argType == CALL){

                try {
                    argument2 = Integer.parseInt(segments[2]);
                }catch (Exception e){
                    throw new IllegalArgumentException("Argument2 is not int");
                }
            }
        }
    }


    public int commandType(){

        if (argType != -1)
            return argType;
        else
            throw new IllegalStateException("No command available");
    }


    public String arg1(){

        if (commandType() != RETURN)
            return argument1;
        else
            throw new IllegalStateException("No argument1 in RETURN command");
    }


    public int arg2(){

        if (commandType() == PUSH || commandType() == POP || commandType() == FUNCTION || commandType() == CALL)
            return argument2;
        else
            throw new IllegalStateException("No argument2 in this type of command");
    }


    public static String noComments(String strIn){

        int position = strIn.indexOf("//");

        if (position != -1)
            strIn = strIn.substring(0, position);

        return strIn;
    }
}