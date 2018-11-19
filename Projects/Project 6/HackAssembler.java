//package HackAssembler;

import java.io.*;

public class HackAssembler{


    public static void main(String[] args) {
        String filename =  args[0];
        String commandType, binaryCommand, newFileName;
        Parser parser;
        PrintWriter writer = null;
        SymbolTable symbolTable = new SymbolTable();

        // Create name of new file
        newFileName = args[0];
        newFileName = newFileName.replaceAll("asm", "hack");

        // initialize writer to file
        try {
            writer = new PrintWriter(newFileName, "UTF-8");
        }catch (IOException e){
            System.out.println("error printing");
        }
      
        // initialize parser object for first pass
        try {
            parser = new Parser(filename);
        }catch (FileNotFoundException e){
            System.out.println("no file error");
            return;
        }

        parser.firstPass(symbolTable);

        //initalize parser for second pass
        try {
            parser = new Parser(filename);
        }catch (FileNotFoundException e){
            System.out.println("no file error");
            return;
        }

        parser.advance();
        while (parser.hasMoreCommands()) {
            commandType = parser.commandType();
            if (commandType == "A_COMMAND") {
                if (parser.getLine().charAt(1) <= '9' && parser.getLine().charAt(1) >= '0')
                    binaryCommand = "0" + parser.aCommandString();
                else{
                    String symbol = parser.getLine().substring(1);
                    if (symbolTable.contains(symbol))
                        binaryCommand = "0" + parser.aCommandInt(symbolTable.getAddress(symbol));
                    else{
                        symbolTable.addEntry(symbol, 0, 'N');
                        binaryCommand = "0" + parser.aCommandInt(symbolTable.getAddress(symbol));
                    }
                }
            } 
                else
                    binaryCommand = "111" + Code.comp(parser.comp()) + Code.dest(parser.dest()) + Code.jump(parser.jump());
            writer.println(binaryCommand);
            parser.advance();
        }
            writer.close();
    }
}
