package org.getalp.lexsema.examples;

import org.nd4j.linalg.factory.Nd4j;

import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public final class Word2VecLexicalAccess {
    private static final Logger logger = LoggerFactory.getLogger(Word2VecLexicalAccess.class);

    private Word2VecLexicalAccess() {
    }


    public static void main(String... args) throws IOException {

        Nd4j.dtype = DataBuffer.Type.FLOAT;

        if (args.length <2) {
            usage();
        }
        WordVectors wv = loadEmbeddings(args[0]);

        Collection<String> neighbours = wv.wordsNearest(args[1],10);
        int numNeigh = 0;

        for (String w : neighbours){
            numNeigh++;
            logger.info("{} -- {}", String.valueOf(numNeigh), w);
        }

    }

    private static WordVectors loadEmbeddings(String path) throws IOException {
        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(path), true, false);
        return word2VecLoader.getWordVectors();
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.Word2VecLexicalAccess [path to folder containing model.bin] [word to lookup]");
        System.exit(1);
    }
}
