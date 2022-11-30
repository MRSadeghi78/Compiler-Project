package ast.block;

import org.objectweb.asm.Label;

import java.util.Stack;

public class IfCases {
    public static Stack<Label> startIfLabel = new Stack<>();
    public static Stack<Label> endIfLabel = new Stack<>();
    public static Stack<Label> startElseLabel = new Stack<>();
    public static Stack<Label> endElseLabel = new Stack<>();
}
