package ast.block;

import org.objectweb.asm.Label;

public class BlockLabel {

    Label startLabel,endLabel;

    public BlockLabel(Label startLabel, Label endLabel) {
        this.startLabel = startLabel;
        this.endLabel = endLabel;
    }

    public Label getStartLabel() {
        return startLabel;
    }

    public void setStartLabel(Label startLabel) {
        this.startLabel = startLabel;
    }

    public Label getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(Label endLabel) {
        this.endLabel = endLabel;
    }


}
