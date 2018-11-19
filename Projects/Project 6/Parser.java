//package HackAssembler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    private String fileName;
    private BufferedReader reader;
    private String currentLine;



    public Parser(String filename) throws FileNotFoundException {
        this.fileName = filename;
        this.reader = new BufferedReader(new FileReader(this.fileName));
        this.currentLine = "0";
    }

    public String getLine (){
        return this.currentLine;
    }

    public boolean hasMoreCommands() {
        return this.currentLine != (null);
    }

    public void firstPass(SymbolTable table) {
        String[] splitLine;
        int lineNum = 0;
        while (this.hasMoreCommands()) {
            try {
                currentLine = reader.readLine();
                if (currentLine == null)
                    return;
                splitLine = currentLine.split("//");
                while (splitLine[0].equals("")) {
                    currentLine = reader.readLine();
                    splitLine = currentLine.split("//");
                }
                splitLine[0] = splitLine[0].replaceAll("\\s+","");
                if (splitLine[0].charAt(0) == '(')
                    table.addEntry(currentLine.substring(1, currentLine.length() - 1), lineNum, 'L'); //add a label entry to symbolTable
                else
                    lineNum++;


            } catch (IOException e) { System.err.println("END OF FILE"); }
        }
    }

    public void advance() {
        String[] splitLine;
        try {
            currentLine = reader.readLine();
            if (currentLine == null)
                return;
            splitLine = currentLine.split("//");
            currentLine = splitLine[0];
            splitLine = currentLine.split("\\(");
            while (splitLine[0].equals("")) {
                currentLine = reader.readLine();
                splitLine = currentLine.split("[//\\(]");
            }
            splitLine[0] = splitLine[0].replaceAll("\\s+","");
            currentLine = splitLine[0];
        } catch (IOException e){System.err.println("END OF FILE");}
    }

    public String aCommandString(){
        String [] numInStringForm = currentLine.split("@");
        int x = Integer.parseInt(numInStringForm[1]);
        StringBuilder sb = new StringBuilder("");
        String binaryNum = Integer.toBinaryString(x);
        for (int i = 0; i < (15 - binaryNum.length()); i++){
            sb.append("0");
        }
        sb.append(binaryNum);
        return sb.toString();
    }

    public String aCommandInt (int value){
        StringBuilder sb = new StringBuilder("");
        String binaryNum = Integer.toBinaryString(value);
        for (int i = 0; i < (15 - binaryNum.length()); i++){
            sb.append("0");
        }
        sb.append(binaryNum);
        return sb.toString();
    }


    public String dest() {
        String [] splitLine = currentLine.split("=");
        return splitLine[0];
    }

    public String comp() {
        String [] splitLine;
        if (currentLine.contains(";")) {
            splitLine = currentLine.split(";");
            return splitLine[0];
        }
        else
            splitLine = currentLine.split("=");
        return splitLine[1];
    }

    public String jump() {
        String [] splitLine;
        if (currentLine.contains(";")) {
            splitLine = currentLine.split(";");
            return splitLine[1];
        }
        else
            return "";
    }

    public String commandType() {
        char commandType = currentLine.charAt(0);
        if (commandType == '@')
            return "A_COMMAND";
        return "C_COMMAND";
    }
}