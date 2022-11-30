package ast.expr.constant;

import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.objectweb.asm.Opcodes;

import static CodeGenerator.CodeGenerator.mVisit;

public class CharConstant extends Constant {

    public CharConstant(Object value) {
        super(value);
        this.value = getChar((String) value);
    }

    @Override
    public Type getResultType() {
        return Type.INT;
    }

    @Override
    public void compile() {
        Logger.log("character constant");
        CodeGenerator.mVisit.visitVarInsn(Opcodes.BIPUSH, ((char) value));
//        CodeGenerator.mVisit.visitInsn(Opcodes.I2C);
    }

    private char getChar(String str) {
        String ch = str.substring(1, str.length() - 1);
        switch (ch) {
            case "\\b":
                return '\b';
            case "\\f":
                return '\f';
            case "\\n":
                return '\n';
            case "\\r":
                return '\r';
            case "\\t":
                return '\t';
            case "\\\\":
                return '\\';
            default:
                return ch.charAt(0);
        }
    }

}
