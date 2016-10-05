package org.getalp.lexsema.ml.optimization.functions.setfunctions;

import org.getalp.lexsema.ml.optimization.functions.Function;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.extentions.Extension;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.input.SetFunctionInput;

public interface SetFunction extends Function {
    double F(SetFunctionInput input);

    Extension getExtension();

    void setExtension(Extension e);
}
