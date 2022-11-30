package ast.expr.binary.arithmatic;

import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static CodeGenerator.CodeGenerator.mVisit;

public class BinaryNot {
    public static void not(){
//        if (!CodeGenerator.getPeekSemanticStack().equals("not"))
//            return;
//        CodeGenerator.getPopSemanticStack();

        Label l1 = new Label();
        Label l2 = new Label();

        String typeText = (String) CodeGenerator.getPopSemanticStack();
        Type type = Type.getType(typeText);

        mVisit.visitJumpInsn(determineOp(type), l1);
        mVisit.visitInsn(Opcodes.ICONST_0);
        mVisit.visitJumpInsn(Opcodes.GOTO, l2);
        mVisit.visitLabel(l1);
        mVisit.visitInsn(Opcodes.ICONST_1);
        mVisit.visitLabel(l2);
    }


    public static  int determineOp(Type type) {
        if (type == Type.INT)
            return Opcodes.IFEQ;
        else
            Logger.error("type mismatch");
        return 0;
    }
}
