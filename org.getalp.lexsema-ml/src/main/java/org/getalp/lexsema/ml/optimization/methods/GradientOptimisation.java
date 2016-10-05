package org.getalp.lexsema.ml.optimization.methods;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.algo.DoubleBlas;
import cern.colt.matrix.tdouble.algo.SmpDoubleBlas;
import cern.jet.math.tdouble.DoubleFunctions;
import org.getalp.lexsema.ml.optimization.functions.Function;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;

public class GradientOptimisation implements OptimisationMethod {

    private DoubleBlas bl = new SmpDoubleBlas();

    public GradientOptimisation() {
    }

    @Override
    public FunctionInput optimise(FunctionInput input, Function f) {
        //        long start = System.currentTimeMillis();
        FunctionInput output = input.copy();
        double lambda = 0.1d;
        DoubleMatrix1D xn = input.getInput();
        DoubleMatrix1D xo;
        DoubleMatrix1D grad;
        double currentNorm = 0;
        double prevNorm = Double.MAX_VALUE;

        // System.err.println("********************************");
        while (prevNorm - currentNorm > lambda) {
            xo = xn;
            grad = f.computeGradient(output);
            // grad.assign(grad, )
            xn = xo.copy()
                    .assign(grad.copy()
                            .assign(DoubleFunctions.mult(lambda)), DoubleFunctions.minus);
            xn.assign(DoubleFunctions.max(0));
            output.setInput(xn);
            currentNorm = bl.dnrm2(xn);
            prevNorm = bl.dnrm2(xo);

            //System.err.println("***********************");
            //System.err.println(" Σ▽f=" + grad.zSum());
            //System.err.println("||▽f||="+bl.dnrm2(grad));
            //System.err.println("||▽Xn||="+currentNorm);
//            System.err.println("f="+((SetFunction)f).getExtension().compute((SetFunctionInput)output));


            //System.err.println("***********************");
        }

        return output;
    }
}
