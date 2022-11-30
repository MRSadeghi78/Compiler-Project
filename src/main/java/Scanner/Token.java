package Scanner;


import Parser.Lexical;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Token implements Lexical {
    private static Scanner scanner;
    private static String currentToken;
    private static int currentTokenID;
    private static String currentParserToken;
    public static int currentLineNumber = 1;

    public static final int NEW_LINE = 1;
    public static final int WHITES_SPACE = 2;
    public static final int SINGLE_LINE_COMMENT = 3;
    public static final int MULTI_LINE_COMMENT = 4;
    public static final int DECIMAL_INTEGER = 5;
    public static final int HEXADECIMAL = 6;
    public static final int REAL_NUMBER = 7;
    public static final int SCIENTIFIC_NOTATION = 8;
    public static final int CHARACTER = 9;
    public static final int STRING = 10;
    public static final int RESERVED_OR_ID_OR_OTHER = 11;
    public static final int IDENTIFIER = 12;
    public static final int OTHER = 13;

    private static final List<String> RESERVED_KEYWORDS = Arrays.asList("int", "short", "long", "float", "double", "char", "string",
            "const", "for", "foreach", "while", "do", "in", "if", "else", "switch", "case", "default", "auto",
            "volatile", "static", "goto", "signed", "bool", "void", "return", "break", "continue", "new", "Sizeof",
            "do", "true", "record", "repeat", "until", "function", "println", "false", "start");
    private static final List<String> OTHER_WORDS = Arrays.asList("==", "!=", "<=", "<", ">", ">=", ".", ",", ":", ";", "[", "]", "++", "=",
            "~", "&", "and", "or", "not", "|", "^", "*", "+", "+=", "--", "-", "-=", "*=", "/=", "/", "%", "begin",
            "end", "(", ")", "\"", "'");


    public Token(FileReader in) {
        scanner = new Scanner(in);
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static int getCurrentTokenID() {
        return currentTokenID;
    }

    public static String getCurrentParserToken() {
        return currentParserToken;
    }

    @Override
    public String nextToken() {
        int tokenId = 0;

        try {
            tokenId = scanner.yylex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tokenId == NEW_LINE)
            currentLineNumber++;
        if (tokenId == WHITES_SPACE || tokenId == NEW_LINE || tokenId == SINGLE_LINE_COMMENT || tokenId == MULTI_LINE_COMMENT)
            return nextToken();

        currentToken = scanner.text;
        System.out.println(currentToken);
        currentTokenID = tokenId;
        return currentParserToken = parserToken(tokenId);
    }

    @Override
    public String currentToken() {
        return currentToken;
    }


    private String parserToken(int tokenId) {
        if (currentToken.equals(","))
            return "comma";
        if (currentTokenID == -1)
            return "$";

        if (RESERVED_KEYWORDS.contains(currentToken) || OTHER_WORDS.contains(currentToken)) return currentToken;
//        if (OTHER_WORDS.contains(currentToken)) {
//            switch (currentToken) {
//                case "-":
//                    return "MINUS";
//                case "+":
//                    return "PLUS";
//                case "*":
//                    return "MULTIPLY";
//                case "/":
//                    return "DIVIDE";
//                case "(":
//                    return "LPAREN";
//                case ")":
//                    return "RPAREN";
//                default:
//                    return currentToken;
//            }
//        }
        switch (tokenId) {
            case DECIMAL_INTEGER:
                return "Integer";
            case HEXADECIMAL:
                return "Hex";
            case REAL_NUMBER:
                return "Real Number";
            case SCIENTIFIC_NOTATION:
                return "Scientific Notation";
            case CHARACTER:
                return "Character";
            case STRING:
                return "String";
            case RESERVED_OR_ID_OR_OTHER:
            case IDENTIFIER:
                return "id";
            default:
                return currentToken;
        }
    }
}
