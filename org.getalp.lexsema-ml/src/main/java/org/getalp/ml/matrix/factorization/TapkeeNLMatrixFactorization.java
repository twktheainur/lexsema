package org.getalp.ml.matrix.factorization;

import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import org.getalp.ml.optimization.org.getalp.util.Matrices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class TapkeeNLMatrixFactorization implements MatrixFactorization {

    private static Logger logger = LoggerFactory.getLogger(TapkeeNLMatrixFactorization.class);

    private DoubleMatrix2D A;
    private DoubleMatrix2D U;
    private DoubleMatrix2D V;
    private Method method;
    private int dimensions = -1;

    public TapkeeNLMatrixFactorization(DoubleMatrix2D a, Method method) {
        A = a;
        this.method = method;
    }

    public TapkeeNLMatrixFactorization(DoubleMatrix2D a, Method method, int dimensions) {
        A = a;
        this.method = method;
        this.dimensions = dimensions;
    }

    @Override
    public DoubleMatrix2D getU() {
        return U;
    }

    @Override
    public DoubleMatrix2D getV() {
        return V;
    }

    private void identityResult() {
        V = A;
    }

    @Override
    public void compute() {
        U = DoubleFactory2D.dense.identity(A.columns());
        if (A.rows() < 4) {
            identityResult();
        } else {
            long time = System.currentTimeMillis();
            File tmpDir = new File(".tmpdata");
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdirs()) {
                    logger.error("Cannot create temporary output location");
                }
            }
            File input = new File(tmpDir, String.format("%d_source.dat", time));
            File output = new File(tmpDir, String.format("%d_output.dat", time));
            File projection = new File(String.format("%d_projection.dat", time));

            writeSourceMatrix(input, A);

            Runtime r = Runtime.getRuntime();
            Process p = null;
            try {
                String command = String.format("tapkee-nle-server%stapkee_nle_server -i %s -o %s -opmat %s -m %s",
                        File.separator,
                        input.getAbsolutePath(), // -i
                        output.getAbsolutePath(), // -o
                        projection.getAbsolutePath(), //-opmat
                        method.getCommand()); // -m
                if (dimensions > 0) {
                    command += String.format(" -td %d", dimensions);
                }
                if (A.rows() < 10) {
                    command += String.format(" -k %d", A.rows() - 1);
                }
                p = r.exec(command);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
            if (p != null) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    logger.error(e.getLocalizedMessage());
                }
                String errOutput = readInputStream(p.getErrorStream());
                if (!errOutput.trim().isEmpty()) {
                    identityResult();
                    logger.error(errOutput);
                }

                readOutputMatrix(output);

                if (!input.delete()) {
                    logger.error("Cannot delete input matrix file");
                }
                if (!output.delete()) {
                    logger.error("Cannot delete projection matrix file");
                }
        /*if(!projection.delete()){
            logger.error("Cannot delete projected matrix file");
        }*/
            }
        }
    }

    private void readOutputMatrix(File output) {
        try (BufferedReader outputBr = new BufferedReader(new FileReader(output))) {
            int rows = A.rows();
            int cols = -1;
            String line = outputBr.readLine();
            int rowNumber = 0;
            while (line != null) {
                String[] fields = line.trim().split(",");
                if (cols == -1) {
                    cols = fields.length;
                    V = new DenseDoubleMatrix2D(rows, cols);
                }
                for (int colNum = 0; colNum < fields.length; colNum++) {
                    V.setQuick(rowNumber, colNum, Double.valueOf(fields[colNum]));
                }
                line = outputBr.readLine();
                rowNumber++;
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private String readInputStream(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader b = new BufferedReader(new InputStreamReader(in))) {
            String line = b.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = b.readLine();
            }
            b.close();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    private void writeSourceMatrix(File destination, DoubleMatrix2D matrix) {
        try (PrintWriter pw = new PrintWriter(destination)) {
            Matrices.matrixCSVWriter(pw, matrix);
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }


    @SuppressWarnings("PublicMethodNotExposedInInterface")
    public enum Method {
        LLE("Locally Linear Embeddings", "lle"),
        NPE("Neighbourhood Preserving Embeddings", "npe"),
        LTSA("Local Tangent Space Alignment", "ltsa"),
        LLTSA("Local Tangent Space Alignment", "lltsa"),
        HLLE("Hessian Locally Linear Embeddings", "hlle"),
        LA("Laplatian Eigenmaps", "la"),
        LPP("Locally Preserving Projections", "lpp"),
        DM("Diffusion Maps", "dm"),
        ISOMAP("isomap", "isomap"),
        L_ISOMAP("Landmark isomap", "l-isomap"),
        MDS("MultiDimensional Scaling", "mds"),
        L_MDS("Landmark MultiDimensional Scaling", "l-mds"),
        SPE("Stochastic Proximity Embedding", "spe"),
        KPCA("Kernel Principal Component Analysis", "kpca"),
        PCA("Principal Component Analysis", "pca"),
        RA("Random Projections", "ra"),
        FA("Factor Analysis", "fa"),
        T_SNE("t Stochastic Neighbourhood Embedding", "t-sne"),
        MS("Manifold Sculpting", "ms");

        private String description;
        private String command;

        Method(String description, String command) {
            this.description = description;
            this.command = command;
        }

        public String getDescription() {
            return description;
        }

        public String getCommand() {
            return command;
        }
    }
}
