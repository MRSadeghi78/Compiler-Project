package ast.expr.compare;

import CodeGenerator.CodeGenerator;

public class Compare {

    public static void Ccmp(){
        String type1 = (String) CodeGenerator.semanticStack.pop();
        String compareType = (String) CodeGenerator.semanticStack.pop();
        String type2 = (String) CodeGenerator.semanticStack.pop();
    }
}
