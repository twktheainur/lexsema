package org.getalp.ml.optimization.functions.setfunctions;


import org.getalp.ml.optimization.functions.Function;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.extentions.Extension;
import org.getalp.ml.optimization.functions.setfunctions.input.SetFunctionInput;

public abstract class SetFunction implements Function {
    private Extension extension;

    protected SetFunction() {
    }

    public double F(FunctionInput input) {
        if (input instanceof SetFunctionInput) {
            return F((SetFunctionInput) input);
        } else {
            return 0d;
        }
    }

    public abstract double F(SetFunctionInput input);

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension e) {
        extension = e;
        e.setSetFunction(this);
    }

    public boolean isDifferentiable() {
        return extension != null && extension.isDifferentiable();
    }

}
