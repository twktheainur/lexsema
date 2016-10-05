package org.getalp.lexsema.ml.optimization.functions.setfunctions.input;


import com.wcohen.ss.api.StringDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommonSubSequences {
    private static final double DELTAZERO = 0.001d;
    private final List<String> first;
    private final List<String> second;
    private final int[][] lengths;
    private double[][] scores;
    private final boolean[][] processed;
    private final boolean computed;
    private double threshold;
    private StringDistance distance;

    public CommonSubSequences(final List<String> first, final List<String> second) {
        this.first = Collections.unmodifiableList(first);
        this.second = Collections.unmodifiableList(second);
        lengths = new int[first.size()][second.size()];
        processed = new boolean[first.size()][second.size()];
        computed = false;
    }

    private void computeDistances() {
        double z = 0;
        if (first != null && second != null) {
            for (int i = 0; i < first.size(); i++) {
                for (int j = 0; j < second.size(); j++) {
                    double score = 0d;
                    if (distance != null) {
                        score = distance.score(distance.prepare(first.get(i)), distance.prepare(second.get(j)));
                        scores[i][j] = score;
                    }else if (first.get(i).equals(second.get(j)) || score > threshold) {
                        if (i == 0 || j == 0) {
                            lengths[i][j] = 1;
                        } else {
                            lengths[i][j] = lengths[i - 1][j - 1] + 1;
                        }
                        if (lengths[i][j] > z) {
                            z = lengths[i][j];
                        }
                    } else {
                        lengths[i][j] = 0;
                    }
                }
            }
        }
    }

    public List<Double> computeSubSequenceLengths() {

        if (!computed) {
            computeDistances();
        }

        List<Double> ret = new ArrayList<>();

        int x = first.size() - 1;
        int y = second.size() - 1;
        double currentSequence = 0;
        while (x >= 0 && y >= 0) {
                                                                                                                    /*The case where the is first single word match at the beginning of the strings*/
            if (lengths[x][y] == 1 && (x == 0 || y == 0) && !processed[x][y]) {
                if (distance != null) {
                    ret.add(scores[x][y]);
                } else {
                    ret.add((double) lengths[x][y]);
                }
                processed[x][y] = true;
            } else {
                int prevX = x == 0 ? 0 : x - 1;
                int prevY = y == 0 ? 0 : y - 1;
                double lengthDifference = lengths[prevX][prevY] - (lengths[x][y] - 1);
                if (lengthDifference < DELTAZERO && lengthDifference > -DELTAZERO && !processed[x][y]) {
                    if (distance != null) {
                        currentSequence += scores[x][y];
                    } else {
                        currentSequence += 1;
                    }
                    int nx = x;
                    int ny = y;
                    while (!(nx <= 0 || ny <= 0 || lengths[nx][ny] == 0)) {
                        processed[nx][ny] = true;
                        ny--;
                        nx--;
                        if (distance != null) {
                            currentSequence += scores[nx][ny];
                        } else {
                            currentSequence += 1;
                        }
                    }
                    ret.add(currentSequence);
                    currentSequence = 0;
                }
            }
            if (y - 1 < 0 && x > 0) {
                x--;
            } else {
                y--;
                if (y == 0 && x > 0) {
                    x--;
                    y = second.size() - 1;
                }
            }
        }
        return ret;
    }


    public void setFuzzyDistance(StringDistance distance, double threshold) {
        this.distance = distance;
        this.threshold = threshold;
        scores = new double[first.size()][second.size()];
    }
}

