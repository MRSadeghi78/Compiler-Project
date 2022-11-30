package ast.expr.constant;

import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;

public class FloatConstant extends Constant {

    public FloatConstant(Object value) {
        super(value);
    }

    @Override
    public Type getResultType() {
        return Type.FLOAT;
    }

    @Override
    public void compile() {
        Logger.log("float constant");
        Float main = (float) value;
//        if (main.equals((float) 0))
//            CodeGenerator.mVisit.visitInsn(Opcodes.FCONST_0);
//        else if (main.equals((float) 1))
//            CodeGenerator.mVisit.visitInsn(Opcodes.FCONST_1);
//        else if (main.equals((float) 2))
//            CodeGenerator.mVisit.visitInsn(Opcodes.FCONST_2);
//        else
            CodeGenerator.mVisit.visitLdcInsn(main);
    }

}
