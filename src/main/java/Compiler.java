import CodeGenerator.CodeGenerator;
import Parser.Parser;
import Scanner.Token;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    private static Token token;
    private static Parser parser;
    private static CodeGenerator codeGenerator;
    private static String PARSE_TABLE_URL = "src/main/java/Parser/table.npt";

    public static void main(String[] args) {
        try {
            token = new Token(new FileReader("test.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        codeGenerator = new CodeGenerator();
        CodeGenerator.initClass();
        parser = new Parser(token, codeGenerator, PARSE_TABLE_URL);
        parser.parse();
        CodeGenerator.writeFinalClassCode();
    }
}
