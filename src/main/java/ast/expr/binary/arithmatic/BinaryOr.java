package ast.expr.binary.arithmatic;

import ast.expr.Expression;
import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;

import static ast.type.Type.*;

public class BinaryOr extends ArithmeticBinaryExpr {

    public BinaryOr(Expression expr1, Expression expr2) {
        super(expr1, expr2);
    }

    @Override
    public void compile() {
        Logger.log("binary or");
        super.compile();
    }

    public static void or(){
        String typeText = (String) CodeGenerator.getPopSemanticStack();
        Type type = Type.getType(typeText);
        CodeGenerator.mVisit.visitInsn(det(type));
    }

    public static int det(Type type){
        if (type == LONG)
            return Opcodes.LOR;
        else if (type == INT || type == BOOL)
            return Opcodes.IOR;
        else
            Logger.error("type mismatch");
        return 0;
    }
    @Override
    public int determineOp(Type type) {
        if (type == LONG)
            return Opcodes.LOR;
        else if (type == INT)
            return Opcodes.IOR;
        else
            Logger.error("type mismatch");
        return 0;
    }

}
