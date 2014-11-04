package org.getalp.util;

import com.wcohen.ss.api.StringDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 10/29/14.
 */
public class CommonSubsequences {
    private int[][] lengths;
    private double[][] scores;
    private boolean[][] processed;

    private boolean computed;

    private List<String> a;
    private List<String> b;
    private double threshold;
    private StringDistance distance;

    public CommonSubsequences(List<String> a, List<String> b) {
        this.a = a;
        this.b = b;
        lengths = new int[a.size()][b.size()];
        processed = new boolean[a.size()][b.size()];
        computed = false;
    }

    private void computeDistances() {
        double z = 0;
        if (a != null && b != null) {
            for (int i = 0; i < a.size(); i++) {
                for (int j = 0; j < b.size(); j++) {
                    double score = 0d;
                    if (distance != null) {
                        score = distance.score(distance.prepare(a.get(i)), distance.prepare(b.get(j)));
                        scores[i][j] = score;
                    }
                    if ((distance == null && a.get(i).equals(b.get(j))) ||
                            (distance != null && score > threshold)) {
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

        int x = a.size() - 1;
        int y = b.size() - 1;
        double currentSequence = 0;
        while (x >= 0 && y >= 0) {
            /*The case where the is a single word match at the beginning of the strings*/
            if (((lengths[x][y] == 1 && (x == 0 || y == 0)) && !processed[x][y])) {
                if (distance != null) {
                    ret.add(scores[x][y]);
                } else {
                    ret.add((double) lengths[x][y]);
                }
                processed[x][y] = true;
            } else {
                int prevX = (x == 0) ? 0 : (x - 1);
                int prevY = (y == 0) ? 0 : y - 1;
                double lengthDifference = lengths[prevX][prevY] - (lengths[x][y] - 1);
                if ((lengthDifference < 0.001d && lengthDifference > -0.001d) && !processed[x][y]) {
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
                    y = b.size() - 1;
                }
            }
        }
        return ret;
    }


    public void setFuzzyDistance(StringDistance distance, double threshold) {
        this.distance = distance;
        this.threshold = threshold;
        scores = new double[a.size()][b.size()];
    }
}
