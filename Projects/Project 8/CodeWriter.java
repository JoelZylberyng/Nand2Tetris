import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CodeWriter {

    private int JumpCounter;
    private PrintWriter printWriter;
    private static final Pattern labelMatcher = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
    private static int labelCount = 0;
    private static String fileName;


    /**
     * Constructor for CodeWriter
     * @param fileOut the file to write to
     */
    public CodeWriter(File fileOut) {

        try {
            fileName = fileOut.getName();
            printWriter = new PrintWriter(fileOut);
            JumpCounter = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(File fileOut){ fileName = fileOut.getName(); }


    /**
     * Write the assembly code for arithmetic commands
     * @param command
     */
    public void writeArithmetic(String command){

        switch (command){
            case "add":
                printWriter.print(arithmeticOperand() + "M=M+D\n");
                break;
            case "sub":
                printWriter.print(arithmeticOperand() + "M=M-D\n");
                break;
            case "and":
                printWriter.print(arithmeticOperand() + "M=M&D\n");
                break;
            case "or":
                printWriter.print(arithmeticOperand() + "M=M|D\n");
                break;
            case "gt":
                printWriter.print(arithmeticCompare("JLE"));
                JumpCounter++;
                break;
            case "lt":
                printWriter.print(arithmeticCompare("JGE"));
                JumpCounter++;
                break;
            case "eq":
                printWriter.print(arithmeticCompare("JNE"));
                JumpCounter++;
                break;
            case "not":
                printWriter.print("@SP\nA=M-1\nM=!M\n");
                break;
            case "neg":
                printWriter.print("D=0\n@SP\nA=M-1\nM=D-M\n");
                break;
            default:
                throw new IllegalArgumentException("Error in function call");
        }

    }

    /**
     * Write the assembly code for PUSH or POP
     * @param command PUSH or POP
     * @param segment
     * @param index
     */
    public void writePushPop(int command, String segment, int index){

        if (command == Parser.PUSH){

            switch(segment){
                case "constant":
                    printWriter.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "local":
                    printWriter.print(pushCodeLine("LCL",index,false));
                    break;
                case "argument":
                    printWriter.print(pushCodeLine("ARG",index,false));
                    break;
                case "this":
                    printWriter.print(pushCodeLine("THIS",index,false));
                    break;
                case "that":
                    printWriter.print(pushCodeLine("THAT",index,false));
                    break;
                case "temp":
                    printWriter.print(pushCodeLine("R5", index + 5,false));
                    break;
                case "static":
                    printWriter.print("@" + fileName + index + "\n" + "D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                default: //command is pointer
                    if (index == 0)
                        printWriter.print(pushCodeLine("THIS",index,true));
                    else
                        printWriter.print(pushCodeLine("THAT",index,true));
            }

        }else if(command == Parser.POP) {
            switch (segment) {
                case "local":
                    printWriter.print(popCodeLine("LCL", index, false));
                    break;
                case "argument":
                    printWriter.print(popCodeLine("ARG", index, false));
                    break;
                case "this":
                    printWriter.print(popCodeLine("THIS", index, false));
                    break;
                case "that":
                    printWriter.print(popCodeLine("THAT", index, false));
                    break;
                case "temp":
                    printWriter.print(popCodeLine("R5", index + 5, false));
                    break;
                case "static":
                    printWriter.print("@" + fileName + index + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                default: // pointer
                    if (index == 0)
                        printWriter.print(popCodeLine("THIS", index, true));
                    else
                        printWriter.print(popCodeLine("THAT", index, true));
            }
            }else
                throw new IllegalArgumentException("Error in function call");
        }



    /**
     * Assembly code for add sub and or
     * @return
     */
    private String arithmeticOperand(){ return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n"; }

    /**
     * Assembly code for gt lt eq
     * @param type JLE JGT JEQ
     * @return
     */
    private String arithmeticCompare(String type){

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@FALSE" + JumpCounter + "\n" +
                "D;" + type + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "@CONTINUE" + JumpCounter + "\n" +
                "0;JMP\n" +
                "(FALSE" + JumpCounter + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "(CONTINUE" + JumpCounter + ")\n";


    }

    /**
     * Assembly code for push local,this,that,argument,temp,pointer,static
     * @param segment
     * @param index
     * @param isDirect Is this command a direct addressing?
     * @return
     */
    private String pushCodeLine(String segment, int index, boolean isDirect){

        String noPointerCode = (isDirect)? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n"+
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

    }

    /**
     * Assembly code for pop local,this,that,argument,temp,pointer,static
     * @param segment
     * @param index
     * @param isDirect Is this command a direct addressing?
     * @return
     */
    private String popCodeLine(String segment, int index, boolean isDirect){

        String noPointerCode = (isDirect)? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + segment + "\n" +
                noPointerCode +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";

    }


    public void writeLabel(String label){
        Matcher m = labelMatcher.matcher(label);
        if (m.find())
            printWriter.print("(" + label +")\n");
        else
            throw new IllegalArgumentException("Wrong label format");
    }



    /**
     * Write assembly code for the 'goto' command
     * @param label
     */
    public void writeGoto(String label){
        Matcher m = labelMatcher.matcher(label);
        if (m.find())
            printWriter.print("@" + label + "\n0;JMP\n");
        else
            throw new IllegalArgumentException("Wrong label format");
    }


    /**
     * Write assembly code for the 'if-goto' command
     * @param label
     */
    public void writeIf(String label){
        Matcher m = labelMatcher.matcher(label);
        if (m.find())
            printWriter.print("@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "@" + label + "\nD;JNE\n");
        else
            throw new IllegalArgumentException("Wrond label format");
    }


    /**
     * Write assembly code for the 'call' command
     * @param functionName
     * @param numArgs
     */
    public void writeCall(String functionName, int numArgs){
        String label = "RETURN_LABEL" + (labelCount++);

        printWriter.print("@" + label + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        printWriter.print(pushCodeLine("LCL",0,true));
        printWriter.print(pushCodeLine("ARG",0,true));
        printWriter.print(pushCodeLine("THIS",0,true));
        printWriter.print(pushCodeLine("THAT",0,true));

        printWriter.print("@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" +
                "@" + numArgs + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n" +
                "@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n" +
                "@" + functionName + "\n" +
                "0;JMP\n" +
                "(" + label + ")\n"
        );

    }


    /**
     * Write assembly code for the 'return' command
     */
    public void writeReturn(){
        printWriter.print("@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                popCodeLine("ARG",0,false) +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                preFrameTemplate("THAT") +
                preFrameTemplate("THIS") +
                preFrameTemplate("ARG") +
                preFrameTemplate("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n");
    }


    /**
     * Write assembly code for the 'function' command
     * @param functionName
     * @param numLocals
     */
    public void writeFunction(String functionName, int numLocals){
        printWriter.print("(" + functionName +")\n");
        for (int i = 0; i < numLocals; i++)
            writePushPop(Parser.PUSH,"constant",0);
    }



    public String preFrameTemplate(String position){

        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + position + "\n" +
                "M=D\n";

    }

    public void writeInit(){
        printWriter.print("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
        writeCall("Sys.init",0);
    }


    /**
     * Close the output file
     */
    public void close(){ printWriter.close();}


}