import com.wcohen.ss.ScaledLevenstein;
import org.getalp.segmentation.Segmenter;
import org.getalp.segmentation.SpaceSegmenter;
import org.getalp.similarity.semantic.string.TverskiIndexSimilarityMeasure;
import org.getalp.similarity.semantic.string.TverskiIndexSimilarityMeasureBuilder;
import org.junit.Test;

/**
 * Created by tchechem on 10/27/14.
 */
public class TverskiIndexSimilarityMeasureTest {


    @Test
    public void distanceTest() {
        TverskiIndexSimilarityMeasure sim = new TverskiIndexSimilarityMeasureBuilder()
                .distance(new ScaledLevenstein())
                .computeRatio(false)
                .alpha(1d)
                .beta(0.0d)
                .gamma(0.0d)
                .fuzzyMatching(true)
                .quadraticWeighting(true)
                .extendedLesk(false)
                .randomInit(false)
                .regularizeOverlapInput(false)
                .optimizeOverlapInput(false)
                .regularizeRelations(false)
                .optimizeRelations(false)
                .isDistance(true)
                .build();
        Segmenter s = new SpaceSegmenter();
        String sense1 = "cereal plants oryza sativa grass family seeds food";
        String sense2 = "specific variety plant ";
        String sense3 = "seeds plant food";
        String otherSeed = "seed";
        String otherUnrelated = "small hard particle";

        String non1 = "a cat was running wild and free cat cat cat cat!";
        String non2 = "The hero valiantly stood his ground until the enemy repelled";

        System.err.println("[rice/cereal | corn/seed ] = " + sim.compute(s.segment(sense1), s.segment(otherSeed)));
        System.err.println("[rice/plant | corn/seed ] = " + sim.compute(s.segment(sense2), s.segment(otherSeed)));
        System.err.println("[rice/seed | corn/seed ] = " + sim.compute(s.segment(sense3), s.segment(otherSeed)));

        System.err.println("[rice/cereal | corn/particle ] = " + sim.compute(s.segment(sense1), s.segment(otherUnrelated)));
        System.err.println("[rice/plant | corn/particle ] = " + sim.compute(s.segment(sense2), s.segment(otherUnrelated)));
        System.err.println("[rice/seed | corn/particle ] = " + sim.compute(s.segment(sense3), s.segment(otherUnrelated)));

        System.err.println("no overlap = " + sim.compute(s.segment(non1), s.segment(non2)));

        assert (true);
    }
}
