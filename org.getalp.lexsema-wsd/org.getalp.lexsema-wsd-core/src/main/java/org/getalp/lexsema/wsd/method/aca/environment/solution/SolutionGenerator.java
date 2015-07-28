package org.getalp.lexsema.wsd.method.aca.environment.solution;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.aca.environment.Environment;

public interface SolutionGenerator {
    Configuration generateSolution(Environment environment, Document document);
}
