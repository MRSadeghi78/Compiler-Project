package ast.expr;

import ast.type.Type;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;

import static ast.type.Type.*;
import static CodeGenerator.CodeGenerator.mVisit;

public class TypeCast {

    public static void typeCast(String st1,String st2){
        Type t1 = Type.getType(st1.toUpperCase());
        Type t2 = Type.getType(st2.toUpperCase());
        doCastCompile(t1,t2);
    }

    public static void doCastCompile(Type type,Type resultType) {

        if (type == resultType)
            return;
        if (type == DOUBLE) {
            if (resultType == FLOAT)
                mVisit.visitInsn(Opcodes.D2F);
            else if (resultType == LONG)
                mVisit.visitInsn(Opcodes.D2L);
            else if (resultType == INT)
                mVisit.visitInsn(Opcodes.D2I);
            else
                Logger.error("type mismatch");
        } else if (type == FLOAT) {
            if (resultType == DOUBLE)
                mVisit.visitInsn(Opcodes.F2D);
            else if (resultType == LONG)
                mVisit.visitInsn(Opcodes.F2L);
            else if (resultType == INT)
                mVisit.visitInsn(Opcodes.F2I);
            else
                Logger.error("type mismatch");
        } else if (type == LONG) {
            if (resultType == DOUBLE)
                mVisit.visitInsn(Opcodes.L2D);
            else if (resultType == FLOAT)
                mVisit.visitInsn(Opcodes.L2F);
            else if (resultType == INT)
                mVisit.visitInsn(Opcodes.L2I);
            else
                Logger.error("type mismatch");
        } else if (type == INT) {
            if (resultType == DOUBLE)
                mVisit.visitInsn(Opcodes.I2D);
            else if (resultType == FLOAT)
                mVisit.visitInsn(Opcodes.I2F);
            else if (resultType == LONG)
                mVisit.visitInsn(Opcodes.I2L);
            else
                Logger.error("type mismatch");
        } else
            Logger.error("type mismatch");
    }
}
