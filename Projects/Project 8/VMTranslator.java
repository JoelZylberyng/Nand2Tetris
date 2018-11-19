import java.io.File;
import java.util.ArrayList;


public class VMTranslator {


    /**
     * The main program, drives the process
     *
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 1) {

            System.out.println("Usage:java VMTranslator <directory or file>");
            System.exit(1);

        }

        File fileSrc = new File(args[0]);
        String fileDest;
        File fileOut;
        CodeWriter writer;
        ArrayList<File> vmFiles = new ArrayList<File>();

        if (fileSrc.isFile()) {

            vmFiles.add(fileSrc);
            fileDest = fileSrc.getAbsolutePath().substring(0, fileSrc.getAbsolutePath().lastIndexOf(".")) + ".asm";

        } else {

            File[] files = fileSrc.listFiles();

            for (File fx : files) {

                if (fx.getAbsolutePath().endsWith(".vm"))
                    vmFiles.add(fx);
            }
            fileDest = fileSrc.getAbsolutePath() + "\\" + fileSrc.getName() + ".asm";
        }

        fileOut = new File(fileDest);
        writer = new CodeWriter(fileOut);

        writer.writeInit();

        for (File f : vmFiles) {

            writer.setFileName(f);
            Parser parser = new Parser(f);
            int type;

            while (parser.hasMoreCommands()) {

                parser.advance();
                type = parser.commandType();

                switch (type) {
                    case Parser.ARITHMETIC:
                        writer.writeArithmetic(parser.arg1());
                        break;
                    case Parser.POP:
                        writer.writePushPop(type, parser.arg1(), parser.arg2());
                        break;
                    case Parser.PUSH:
                        writer.writePushPop(type, parser.arg1(), parser.arg2());
                        break;
                    case Parser.LABEL:
                        writer.writeLabel(parser.arg1());
                        break;
                    case Parser.GOTO:
                        writer.writeGoto(parser.arg1());
                        break;
                    case Parser.IF:
                        writer.writeIf(parser.arg1());
                        break;
                    case Parser.FUNCTION:
                        writer.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    case Parser.RETURN:
                        writer.writeReturn();
                        break;
                    case Parser.CALL:
                        writer.writeCall(parser.arg1(), parser.arg2());
                        break;
                    default:
                        System.exit(1);
                }

            }

        }
        writer.close();
    }
}