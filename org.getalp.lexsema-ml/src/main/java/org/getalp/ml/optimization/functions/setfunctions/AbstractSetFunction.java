package org.getalp.ml.optimization.functions.setfunctions;


import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.extentions.Extension;
import org.getalp.ml.optimization.functions.setfunctions.input.SetFunctionInput;

public abstract class AbstractSetFunction implements SetFunction {
    private Extension extension;

    protected AbstractSetFunction() {
    }

    @Override
    public double F(FunctionInput input) {
        if (input instanceof SetFunctionInput) {
            return F((SetFunctionInput) input);
        } else {
            return 0d;
        }
    }

    @Override
    public Extension getExtension() {
        return extension;
    }

    @Override
    public void setExtension(Extension e) {
        extension = e;
        e.setSetFunction(this);
    }

    @Override
    public boolean isDifferentiable() {
        return extension != null && extension.isDifferentiable();
    }

}
