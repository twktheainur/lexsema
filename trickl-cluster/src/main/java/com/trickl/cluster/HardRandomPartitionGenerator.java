/*
 * This file is part of the Trickl Open Source Libraries.
 *
 * Trickl Open Source Libraries - http://open.trickl.com/
 *
 * Copyright (C) 2011 Tim Gee.
 *
 * Trickl Open Source Libraries are free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trickl Open Source Libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.trickl.cluster;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.random.tdouble.DoubleUniform;
import cern.jet.random.tdouble.engine.DoubleMersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;

public class HardRandomPartitionGenerator implements PartitionGenerator {

    private DoubleRandomEngine randomEngine;

    public HardRandomPartitionGenerator() {
        randomEngine = new DoubleMersenneTwister();
    }

    @Override
    public void generate(DoubleMatrix2D partition) {
        // Initialise U randomly
        partition.assign(0);

        DoubleUniform uniform = new DoubleUniform(randomEngine);

        for (int i = 0; i < partition.rows(); ++i) {
            // Randomise
            int k = uniform.nextIntFromTo(0, partition.columns() - 1);
            partition.setQuick(i, k, 1);
        }
    }

    public DoubleRandomEngine getRandomEngine() {
        return randomEngine;
    }

    @Override
    public void setRandomEngine(DoubleRandomEngine random) {
        randomEngine = random;
    }
}
