package ast.block;

import CodeGenerator.CodeGenerator;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import symtab.SymbolTable;
import symtab.TableStack;

import java.util.HashMap;
import java.util.Stack;

public class repeatLoop {
    private static Stack<Label> loopStartLabel = new Stack<>();
    private static Stack<Label> loopEndLabel = new Stack<>();
    public static void startRepeat() {
        TableStack.getInstance().pushSymbolTable(new SymbolTable(new HashMap<>()));
        Label startLabel = new Label();
        Label endLabel = new Label();
        BlockLabel loopBlock = new BlockLabel(startLabel,endLabel);
        CodeGenerator.blockLabels.push(loopBlock);
        loopStartLabel.push(startLabel);
        loopEndLabel.push(endLabel);
        CodeGenerator.mVisit.visitLabel(startLabel);
    }

    public static void endRepeat(){
        CodeGenerator.mVisit.visitJumpInsn(Opcodes.IFNE, loopStartLabel.pop());
        TableStack.getInstance().popSymbolTable();
        CodeGenerator.mVisit.visitLabel(loopEndLabel.pop());
        CodeGenerator.blockLabels.pop();
    }
}
