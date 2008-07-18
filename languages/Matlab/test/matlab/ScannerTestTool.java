package matlab;

import java.io.*;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 */
public class ScannerTestTool {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java matlab.ScannerTestTool {basename}");
            System.exit(1);
        }
        String basename = args[0];
        try {
            BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
            MatlabLexer lexer = new MatlabLexer(new ANTLRReaderStream(in));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
            List<TranslationProblem> problems = lexer.getProblems();
            if(problems.isEmpty()) {
                for(Token tok : (List<Token>) tokens.getTokens()) {
                    printToken(out, tok);
                }
            } else {
                for(TranslationProblem prob : problems) {
                    out.print('~');
                    out.print(' ');
                    out.print(prob.getLine());
                    out.print(' ');
                    out.println(prob.getColumn());
                }
            }
            out.close();
            in.close();
            System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static void printToken(PrintWriter out, Token tok) throws IOException {
        out.print(MatlabParser.tokenNames[tok.getType()]);
        out.print(' ');
        int startLine = tok.getLine();
        int startCol = tok.getCharPositionInLine() + 1;
        TextPosition lastPos = getLastPosition(tok.getText());
        if(lastPos.getLine() == 1) {
            out.print(startLine);
            out.print(' ');
            out.print(startCol);
            out.print(' ');
            out.print(lastPos.getColumn());
        } else {
            out.print(startLine);
            out.print(' ');
            out.print(startCol);
            out.print(' ');
            out.print(startLine + lastPos.getLine() - 1);
            out.print(' ');
            out.print(startCol + lastPos.getColumn() - 1);
        }
        out.print(' ');
        out.print('=');
        out.print(stringifyValue(tok.getText()));
        out.println();
    }

    public static String stringifyValue(Object value) {
        if(value == null) {
            return null;
        }
        return value.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }
    
    public static TextPosition getLastPosition(String text) throws IOException {
        TrivialScanner scanner = new TrivialScanner(new StringReader(text));
        TextPosition lastPos = null;
        while(true) {
            TextPosition temp = scanner.nextPos();
            if(temp == null) {
                break;
            }
            lastPos = temp;
        }
        return lastPos;
    }
}
