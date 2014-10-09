package org.getalp.optimisation.functions.setfunctions;


import org.getalp.optimisation.functions.Function;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.optimisation.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimisation.functions.setfunctions.submodular.extentions.Extension;

public abstract class SetFunction implements Function {
    private Extension extension;

    protected SetFunction() {
    }

    public double F(FunctionInput input){
        if(input instanceof SetFunctionInput){
            return F((SetFunctionInput)input);
        } else {
            return 0d;
        }
    }

    public abstract double F(SetFunctionInput input);

    public Extension getExtension(){
        return extension;
    }
    public void setExtension(Extension e){
        this.extension = e;
        e.setSetFunction(this);
    }

    public boolean isDifferentiable(){
        return extension!=null && extension.isDifferentiable();
    }

}
