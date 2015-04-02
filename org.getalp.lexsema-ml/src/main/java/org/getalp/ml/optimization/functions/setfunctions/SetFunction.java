package org.getalp.ml.optimization.functions.setfunctions;

import org.getalp.ml.optimization.functions.Function;
import org.getalp.ml.optimization.functions.setfunctions.extentions.Extension;
import org.getalp.ml.optimization.functions.setfunctions.input.SetFunctionInput;

public interface SetFunction extends Function {
    double F(SetFunctionInput input);

    Extension getExtension();

    void setExtension(Extension e);
}
