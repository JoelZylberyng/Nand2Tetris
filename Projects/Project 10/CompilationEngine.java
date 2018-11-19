import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class CompilationEngine {

    private PrintWriter printWriter;
    private PrintWriter tokenPrintWriter;
    private JackTokenizer tokenizer;


    public CompilationEngine(File inputFile, File outputFile, File outputTokens) {

        try {

            tokenizer = new JackTokenizer(inputFile);
            printWriter = new PrintWriter(outputFile);
            tokenPrintWriter = new PrintWriter(outputTokens);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void compileType(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.KEYWORD && (tokenizer.keyWord() == JackTokenizer.INT || tokenizer.keyWord() == JackTokenizer.CHAR || tokenizer.keyWord() == JackTokenizer.BOOLEAN)){

            printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
            tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");

        }

        if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER){

            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        }
    }

    public void compileClass(){

        tokenizer.advance();

        printWriter.print("<class>\n");
        tokenPrintWriter.print("<tokens>\n");
        printWriter.print("<keyword>class</keyword>\n");
        tokenPrintWriter.print("<keyword>class</keyword>\n");

        tokenizer.advance();

        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        writeSymbol('{');
        compileClassVarDec();
        compileSubroutine();
        writeSymbol('}');
        tokenPrintWriter.print("</tokens>\n");
        printWriter.print("</class>\n");

        printWriter.close();
        tokenPrintWriter.close();
    }

    private void compileClassVarDec(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '}'){

            tokenizer.itDown();
            return;
        }


        if (tokenizer.keyWord() == JackTokenizer.CONSTRUCTOR || tokenizer.keyWord() == JackTokenizer.FUNCTION || tokenizer.keyWord() == JackTokenizer.METHOD){

            tokenizer.itDown();
            return;
        }

        printWriter.print("<classVarDec>\n");
        printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        compileType();

            while(true) {

                tokenizer.advance();

                printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

                tokenizer.advance();

                if (tokenizer.symbol() == ',') {

                    printWriter.print("<symbol>,</symbol>\n");
                    tokenPrintWriter.print("<symbol>,</symbol>\n");

                } else {

                    printWriter.print("<symbol>;</symbol>\n");
                    tokenPrintWriter.print("<symbol>;</symbol>\n");
                    break;
                }
            }

        printWriter.print("</classVarDec>\n");
        compileClassVarDec();
    }

    private void compileSubroutine(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '}'){

            tokenizer.itDown();
            return;
        }

        printWriter.print("<subroutineDec>\n");
        printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.KEYWORD && tokenizer.keyWord() == JackTokenizer.VOID){

            printWriter.print("<keyword>void</keyword>\n");
            tokenPrintWriter.print("<keyword>void</keyword>\n");

        }else {

            tokenizer.itDown();
            compileType();

        }

        tokenizer.advance();

        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        writeSymbol('(');
        printWriter.print("<parameterList>\n");
        compileParameterList();
        printWriter.print("</parameterList>\n");
        writeSymbol(')');
        compileSubroutineBody();
        printWriter.print("</subroutineDec>\n");
        compileSubroutine();
    }

    private void compileSubroutineBody(){
        printWriter.print("<subroutineBody>\n");
        writeSymbol('{');
        compileVarDec();
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        writeSymbol('}');
        printWriter.print("</subroutineBody>\n");
    }

    private void compileStatement(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '}'){

            tokenizer.itDown();
            return;

        }

            switch (tokenizer.keyWord()){
                case JackTokenizer.LET:
                    compileLet();
                    break;
                case JackTokenizer.IF:
                    compileIf();
                    break;
                case JackTokenizer.WHILE:
                    compilesWhile();
                    break;
                case JackTokenizer.DO:
                    compileDo();
                    break;
                case JackTokenizer.RETURN:
                    compileReturn();
                    break;
                default:
                    System.out.println("error in file");
            }

        compileStatement();
    }

    private void compileParameterList(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == ')'){

            tokenizer.itDown();
            return;
        }

        tokenizer.itDown();

            while(true) {
                compileType();

                tokenizer.advance();

                printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

                tokenizer.advance();

                if (tokenizer.symbol() == ',') {

                    printWriter.print("<symbol>,</symbol>\n");
                    tokenPrintWriter.print("<symbol>,</symbol>\n");

                } else {

                    tokenizer.itDown();
                    break;

                }
            }
    }

    private void compileVarDec(){
        tokenizer.advance();

        if (tokenizer.tokenType() != JackTokenizer.KEYWORD || tokenizer.keyWord() != JackTokenizer.VAR){

            tokenizer.itDown();
            return;

        }

        printWriter.print("<varDec>\n");
        printWriter.print("<keyword>var</keyword>\n");
        tokenPrintWriter.print("<keyword>var</keyword>\n");
        compileType();

            while(true) {

                tokenizer.advance();
                printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
                tokenizer.advance();

                if (tokenizer.symbol() == ',') {

                    printWriter.print("<symbol>,</symbol>\n");
                    tokenPrintWriter.print("<symbol>,</symbol>\n");

                } else {

                    printWriter.print("<symbol>;</symbol>\n");
                    tokenPrintWriter.print("<symbol>;</symbol>\n");
                    break;
                }
            }

        printWriter.print("</varDec>\n");
        compileVarDec();
    }

    private void compileDo(){

        printWriter.print("<doStatement>\n");
        printWriter.print("<keyword>do</keyword>\n");
        tokenPrintWriter.print("<keyword>do</keyword>\n");
        compileSubroutineCall();
        writeSymbol(';');
        printWriter.print("</doStatement>\n");
    }

    private void compileLet(){

        printWriter.print("<letStatement>\n");
        printWriter.print("<keyword>let</keyword>\n");
        tokenPrintWriter.print("<keyword>let</keyword>\n");

        tokenizer.advance();

        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        tokenizer.advance();

        boolean expExist = false;

        if (tokenizer.symbol() == '['){

            expExist = true;
            printWriter.print("<symbol>[</symbol>\n");
            tokenPrintWriter.print("<symbol>[</symbol>\n");
            compileExpression();

            tokenizer.advance();

            if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == ']'){

                printWriter.print("<symbol>]</symbol>\n");
                tokenPrintWriter.print("<symbol>]</symbol>\n");

            }
        }

        if (expExist)
            tokenizer.advance();

        printWriter.print("<symbol>=</symbol>\n");
        tokenPrintWriter.print("<symbol>=</symbol>\n");
        compileExpression();
        writeSymbol(';');
        printWriter.print("</letStatement>\n");
    }

    private void compilesWhile(){

        printWriter.print("<whileStatement>\n");
        printWriter.print("<keyword>while</keyword>\n");
        tokenPrintWriter.print("<keyword>while</keyword>\n");
        writeSymbol('(');
        compileExpression();
        writeSymbol(')');
        writeSymbol('{');
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        writeSymbol('}');
        printWriter.print("</whileStatement>\n");
    }

    private void compileReturn(){
        printWriter.print("<returnStatement>\n");
        printWriter.print("<keyword>return</keyword>\n");
        tokenPrintWriter.print("<keyword>return</keyword>\n");

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == ';'){

            printWriter.print("<symbol>;</symbol>\n");
            tokenPrintWriter.print("<symbol>;</symbol>\n");
            printWriter.print("</returnStatement>\n");
            return;

        }

        tokenizer.itDown();
        compileExpression();
        writeSymbol(';');
        printWriter.print("</returnStatement>\n");
    }

    private void compileIf(){

        printWriter.print("<ifStatement>\n");
        printWriter.print("<keyword>if</keyword>\n");
        tokenPrintWriter.print("<keyword>if</keyword>\n");
        writeSymbol('(');
        compileExpression();
        writeSymbol(')');
        writeSymbol('{');
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        writeSymbol('}');

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.KEYWORD && tokenizer.keyWord() == JackTokenizer.ELSE){

            printWriter.print("<keyword>else</keyword>\n");
            tokenPrintWriter.print("<keyword>else</keyword>\n");
            writeSymbol('{');
            printWriter.print("<statements>\n");
            compileStatement();
            printWriter.print("</statements>\n");
            writeSymbol('}');

        }else {
            tokenizer.itDown();
        }

        printWriter.print("</ifStatement>\n");
    }

    private void compileTerm(){

        printWriter.print("<term>\n");

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER){

            String tempId = tokenizer.identifier();

            tokenizer.advance();

            if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '['){

                printWriter.print("<identifier>" + tempId + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + tempId + "</identifier>\n");
                printWriter.print("<symbol>[</symbol>\n");
                tokenPrintWriter.print("<symbol>[</symbol>\n");
                compileExpression();
                writeSymbol(']');

            }else if (tokenizer.tokenType() == JackTokenizer.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')){

                tokenizer.itDown();tokenizer.itDown();
                compileSubroutineCall();

            }else {

                printWriter.print("<identifier>" + tempId + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + tempId + "</identifier>\n");
                tokenizer.itDown();
            }

        }else{

            if (tokenizer.tokenType() == JackTokenizer.INT_CONST){

                printWriter.print("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");
                tokenPrintWriter.print("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");

            }else if (tokenizer.tokenType() == JackTokenizer.STRING_CONST){

                printWriter.print("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");
                tokenPrintWriter.print("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");

            }else if(tokenizer.tokenType() == JackTokenizer.KEYWORD && (tokenizer.keyWord() == JackTokenizer.TRUE
                    || tokenizer.keyWord() == JackTokenizer.FALSE || tokenizer.keyWord() == JackTokenizer.NULL || tokenizer.keyWord() == JackTokenizer.THIS)){

                printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
                tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");

            }else if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '('){

                printWriter.print("<symbol>(</symbol>\n");
                tokenPrintWriter.print("<symbol>(</symbol>\n");
                compileExpression();
                writeSymbol(')');

            }else if (tokenizer.tokenType() == JackTokenizer.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')){

                printWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                tokenPrintWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                compileTerm();
            }
        }

        printWriter.print("</term>\n");
    }

    private void compileSubroutineCall(){

        tokenizer.advance();

        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '('){

            printWriter.print("<symbol>(</symbol>\n");
            tokenPrintWriter.print("<symbol>(</symbol>\n");
            printWriter.print("<expressionList>\n");
            compileExpressionList();
            printWriter.print("</expressionList>\n");
            writeSymbol(')');

        }else if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == '.'){

            printWriter.print("<symbol>.</symbol>\n");
            tokenPrintWriter.print("<symbol>.</symbol>\n");
            tokenizer.advance();
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            writeSymbol('(');
            printWriter.print("<expressionList>\n");
            compileExpressionList();
            printWriter.print("</expressionList>\n");
            writeSymbol(')');
        }
    }

    private void compileExpression(){

        printWriter.print("<expression>\n");
        compileTerm();

            while(true) {

                tokenizer.advance();

                if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.isOperand()) {

                    if (tokenizer.symbol() == '>') {

                        printWriter.print("<symbol>&gt;</symbol>\n");
                        tokenPrintWriter.print("<symbol>&gt;</symbol>\n");

                    } else if (tokenizer.symbol() == '<') {

                        printWriter.print("<symbol>&lt;</symbol>\n");
                        tokenPrintWriter.print("<symbol>&lt;</symbol>\n");

                    } else if (tokenizer.symbol() == '&') {

                        printWriter.print("<symbol>&amp;</symbol>\n");
                        tokenPrintWriter.print("<symbol>&amp;</symbol>\n");

                    } else {

                        printWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                        tokenPrintWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                    }
                    compileTerm();

                } else {
                    tokenizer.itDown();
                    break;
                }
            }

        printWriter.print("</expression>\n");
    }

    private void compileExpressionList(){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == ')'){
            tokenizer.itDown();

        }else {

            tokenizer.itDown();
            compileExpression();

                while(true) {

                    tokenizer.advance();

                    if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == ',') {

                        printWriter.print("<symbol>,</symbol>\n");
                        tokenPrintWriter.print("<symbol>,</symbol>\n");
                        compileExpression();

                    } else {
                        tokenizer.itDown();
                        break;
                    }
                }
        }
    }

    private void writeSymbol(char symbol){

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.SYMBOL && tokenizer.symbol() == symbol){

            printWriter.print("<symbol>" + symbol + "</symbol>\n");
            tokenPrintWriter.print("<symbol>" + symbol + "</symbol>\n");
        }
    }
}