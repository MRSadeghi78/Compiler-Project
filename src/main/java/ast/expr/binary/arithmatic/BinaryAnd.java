package ast.expr.binary.arithmatic;

import ast.expr.Expression;
import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;

import static ast.type.Type.*;

public class BinaryAnd extends ArithmeticBinaryExpr {

    public BinaryAnd(Expression expr1, Expression expr2) {
        super(expr1, expr2);
    }

    @Override
    public void compile() {
        Logger.log("binary and");
        super.compile();
    }

    public static void and(){
        String typeText = (String) CodeGenerator.getPopSemanticStack();
        Type type = Type.getType(typeText);
        CodeGenerator.mVisit.visitInsn(det(type));
    }

    private static int det(Type type){
        if (type == LONG)
            return Opcodes.LAND;
        else if (type == INT || type == BOOL)
            return Opcodes.IAND;
        else
            Logger.error("type mismatch");
        return 0;
    }

    @Override
    public int determineOp(Type type) {
        if (type == LONG)
            return Opcodes.LAND;
        else if (type == INT)
            return Opcodes.IAND;
        else
            Logger.error("type mismatch");
        return 0;
    }

}
