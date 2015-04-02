package org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation;


import org.getalp.lexsema.io.goldstandard.GoldStandardData;
import org.getalp.lexsema.io.goldstandard.GoldStandardEntry;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SemevalGoldStandard implements GoldStandard {

    private static final Logger logger = LoggerFactory.getLogger(SemevalGoldStandard.class);
    private GoldStandardData reference;

    public SemevalGoldStandard(GoldStandardData goldStandardData) {
        reference = goldStandardData;
    }

    @Override
    public List<Integer> matches(Configuration c) {
        Document configurationDocument = c.getDocument();
        int cSize = c.size();
        List<GoldStandardEntry> entries = reference.getTextData(getConfigurationDocId(configurationDocument));
        List<String> wsdAssignments = getAssignedIds(c, configurationDocument);
        List<Integer> matching = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if (i >= cSize || assignmentEmpty(wsdAssignments.get(i))) {
                matching.add(-1);
            } else if (i < cSize && assignmentMatches(entries.get(i).getAnnotation(), wsdAssignments.get(i))) {
                matching.add(1);
            } else {
                matching.add(0);
            }
        }
        return matching;
    }

    private void outputDetailedResults(String id, double score, String guess, String key) {
        logger.info(String.format("%sscore for \"%s\": %.3f%s   key\t=%s%s   guess\t=%s",
                System.lineSeparator(), id, score, System.lineSeparator(), key, System.lineSeparator(), guess));
    }

    private boolean assignmentEmpty(String assignment) {
        return assignment.isEmpty();
    }

    private boolean assignmentMatches(String goldAssignment, CharSequence wsdAssignment) {
        return goldAssignment.contains(" ") && goldAssignment.contains(wsdAssignment) ||
                !goldAssignment.contains(" ") && goldAssignment.equals(wsdAssignment);
    }

    private List<String> getAssignedIds(Configuration c, Document document) {
        List<String> assignments = new ArrayList<>();
        for (int i = 0; i < c.size(); i++) {
            int assignment = c.getAssignment(i);
            if (assignment >= 0) {
                assignments.add(getSenseId(document.getSenses(0, i).get(assignment)));
            } else {
                assignments.add("");
            }
        }
        return assignments;
    }

    private String getSenseId(Sense sense) {
        return sense.getId();
    }

    private String getConfigurationDocId(Document document) {
        return document.getId();
    }
}
