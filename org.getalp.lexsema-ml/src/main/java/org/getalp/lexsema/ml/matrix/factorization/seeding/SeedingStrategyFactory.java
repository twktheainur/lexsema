/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.getalp.lexsema.ml.matrix.factorization.seeding;

/**
 * A factory that produces objects implementing the {@link SeedingStrategy}
 * interface.
 */
public interface SeedingStrategyFactory {
    /**
     * Creates a {@link SeedingStrategy}.
     */
    public SeedingStrategy createSeedingStrategy();
}
