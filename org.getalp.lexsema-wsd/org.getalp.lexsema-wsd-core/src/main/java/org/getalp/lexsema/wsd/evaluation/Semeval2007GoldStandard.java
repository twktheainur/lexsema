package org.getalp.lexsema.wsd.evaluation;


import org.getalp.lexsema.io.goldstandard.Semeval2007GoldStandardData;

public class Semeval2007GoldStandard extends SemevalGoldStandard {
    public Semeval2007GoldStandard() {
        super(new Semeval2007GoldStandardData());
    }
}
