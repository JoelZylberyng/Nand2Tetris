import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class CodeWriter {

    private int JumpCounter;
    private PrintWriter printWriter;
	private static String fileName;


    /**
     * Constructor for CodeWriter
     * @param fileOut the file to write to
     */
    public CodeWriter(File fileOut) {

        try {
            printWriter = new PrintWriter(fileOut);
            JumpCounter = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

	
	public void setFileName(File fileOut){ fileName = fileOut.getName(); }

    /**
     * Write the assembly code for given arithmetic command
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
     * Write the assembly code of the given command
     * where the command is either PUSH or POP
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
                    printWriter.print(pushCodeLine(String.valueOf(16 + index),index,true));
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
                    printWriter.print(popCodeLine(String.valueOf(16 + index), index, true));
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
     * Template for add sub and or
     * @return
     */
    private String arithmeticOperand(){ return "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n"; }

    /**
     * Template for gt lt eq
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
     * Template for push local,this,that,argument,temp,pointer,static
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
     * Template for pop local,this,that,argument,temp,pointer,static
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

    /**
     * Close the output file
     */
    public void close(){ printWriter.close();}


}