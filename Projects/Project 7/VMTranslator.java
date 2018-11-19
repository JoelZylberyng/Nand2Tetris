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


        for (File f : vmFiles) {

            writer.setFileName(f);
            Parser parser = new Parser(f);
            int type;

            while (parser.hasMoreCommands()) {
            parser.advance();
            type = parser.commandType();
            if (type == Parser.ARITHMETIC)
                writer.writeArithmetic(parser.arg1());
            else if (type == Parser.POP || type == Parser.PUSH)
                writer.writePushPop(type, parser.arg1(), parser.arg2());
        }

        }
        writer.close();
    }
}