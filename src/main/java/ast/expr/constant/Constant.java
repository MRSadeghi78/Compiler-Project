package ast.expr.constant;

import ast.expr.Expression;

public abstract class Constant extends Expression {

    protected Object value;

    public Constant(Object value) {
        this.value = value;
    }

}
