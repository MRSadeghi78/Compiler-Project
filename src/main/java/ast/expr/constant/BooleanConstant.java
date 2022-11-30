package ast.expr.constant;

import ast.type.Type;
import CodeGenerator.CodeGenerator;
import CodeGenerator.Logger;
import org.objectweb.asm.Opcodes;
import Scanner.Token;

public class BooleanConstant extends Constant {

    public BooleanConstant(Object value) {
        super(value);
    }

    @Override
    public Type getResultType() {
        return Type.INT;
    }

    @Override
    public void compile() {
        Logger.log("boolean constant");
//        CodeGenerator.mVisit.visitVarInsn(Opcodes.BIPUSH, (Boolean) this.value ? 1 : 0);
        CodeGenerator.mVisit.visitInsn((Boolean) this.value ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
    }

}
