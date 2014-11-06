package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.Disambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.io.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public abstract class SequentialDisambiguator implements Disambiguator {

    private int window;
    ExecutorService threadPool;

    List<Future> runningTasks;


    protected SequentialDisambiguator(int window, int numThreads) {
        this.window = window;
        threadPool = Executors.newFixedThreadPool(numThreads);
    }

    protected SequentialDisambiguator(int window, ExecutorService threadPool) {
        this.window = window;
        this.threadPool = threadPool;
    }

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, null);
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        boolean progressChecked = false;
        if (c == null) {
            c = new Configuration(document);
        }
        runningTasks = new LinkedList<>();
        int totalWords = 0;
        for (int i = 0; i < document.getLexicalEntries().size(); i++) {
            int start = i <= window ? 0 : i - window;
            int end = i + window < document.getLexicalEntries().size() ? i + window : document.getLexicalEntries().size();
            if (c.getAssignment(i) == -1) {
                if (document.getSenses(i).size() == 1) {
                    c.setSense(i, 0);
                } else {
                    runningTasks.add(threadPool.submit(getEntryDisambiguator(start, end, i, c, document)));
                }
                totalWords++;
            }
        }
        int completedEntries = 0;
        while (!progressChecked || !runningTasks.isEmpty()) {
            for (int i = 0; i < runningTasks.size(); ) {
                Future current = runningTasks.get(i);
                if (current.isDone()) {
                    runningTasks.remove(i);
                    completedEntries++;
                    String progress = String.format("\tDisambiguating: %.2f%%", (double) completedEntries / (double) totalWords * 100d);
                    System.err.print(progress + "\r");
                } else {
                    i++;
                }
            }
            progressChecked = true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    protected abstract SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d);

    @Override
    public void release() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
