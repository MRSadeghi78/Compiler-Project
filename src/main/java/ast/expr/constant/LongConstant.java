package ast.expr.constant;

import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;

public class LongConstant extends Constant {

    public LongConstant(Object value) {
        super(value);
    }

    @Override
    public Type getResultType() {
        return Type.LONG;
    }

    @Override
    public void compile() {
        Logger.log("Long integer constant");
        long main = (long) value;
//        switch ((int) main) {
//            case 0:
//                CodeGenerator.mVisit.visitInsn(Opcodes.LCONST_0);
//                break;
//            case 1:
//                CodeGenerator.mVisit.visitInsn(Opcodes.LCONST_1);
//                break;
//            default: {
//                if (main > Byte.MIN_VALUE && main < Byte.MAX_VALUE)
//                    CodeGenerator.mVisit.visitVarInsn(Opcodes.BIPUSH, (int) main);
//                else if (main > Short.MIN_VALUE && main < Short.MAX_VALUE)
//                    CodeGenerator.mVisit.visitVarInsn(Opcodes.SIPUSH, (int) main);
//                else
                    CodeGenerator.mVisit.visitLdcInsn(value);
//            }
//        }
    }

}

