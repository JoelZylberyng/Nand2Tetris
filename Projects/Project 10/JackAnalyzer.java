import java.io.File;
import java.util.ArrayList;

public class JackAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {

            System.out.println("Usage:java JackAnalyzer <directory or file>");
            System.exit(1);

        }

        File fileSrc = new File(args[0]);
        String fileDest, tokenFileDest;
        File fileOut, tokenFileOut;
        ArrayList<File> jackFiles = new ArrayList<File>();

        if (fileSrc.isFile()) {

            jackFiles.add(fileSrc);

        } else {

            File[] files = fileSrc.listFiles();

            for (File fx : files) {

                if (fx.getAbsolutePath().endsWith(".jack"))
                    jackFiles.add(fx);
            }
        }
        for (File f: jackFiles) {

            fileDest = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".xml";
            tokenFileDest = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + "T.xml";
            fileOut = new File(fileDest);
            tokenFileOut = new File(tokenFileDest);

            CompilationEngine compilationEngine = new CompilationEngine(f, fileOut, tokenFileOut);
            compilationEngine.compileClass();

        }
    }
}
