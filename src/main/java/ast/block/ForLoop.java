package ast.block;

import CodeGenerator.CodeGenerator;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import symtab.SymbolTable;
import symtab.TableStack;

import java.util.HashMap;
import java.util.Stack;

import static CodeGenerator.CodeGenerator.mVisit;

public class ForLoop {

    public static Stack<Label> startLabel = new Stack<>();
    public static Stack<Label> endLabel = new Stack<>();
    public static Stack<Label> loopStart = new Stack<>();
    public static Stack<Label> loopBody = new Stack<>();

    public static void initForLoop() {
        TableStack.getInstance().pushSymbolTable(new SymbolTable(new HashMap<>()));
        loopStart.push(new Label());
        loopBody.push(new Label());
        mVisit.visitLabel(loopStart.peek());
        Label startBlockFor = new Label();
        Label endBlockFor = new Label();
        startLabel.push(startBlockFor);
        endLabel.push(endBlockFor);
        CodeGenerator.blockLabels.push(new BlockLabel(startBlockFor,endBlockFor));
    }

    public static void forConditions(){

        mVisit.visitJumpInsn(Opcodes.IFEQ, endLabel.peek());
        mVisit.visitJumpInsn(Opcodes.GOTO, loopBody.peek());
        CodeGenerator.mVisit.visitLabel(startLabel.peek());
    }

    public static void forUpdate() {
        mVisit.visitJumpInsn(Opcodes.GOTO, loopStart.pop());
        mVisit.visitLabel(loopBody.pop());
    }

    public static void completeFor() {
        mVisit.visitJumpInsn(Opcodes.GOTO, startLabel.pop());
        CodeGenerator.mVisit.visitLabel(endLabel.pop());
        TableStack.getInstance().popSymbolTable();
    }
}
