package org.getalp.util;

import com.wcohen.ss.api.StringDistance;
import org.getalp.encoding.CodePointWrapper;

import java.util.ArrayList;
import java.util.List;

public final class SubSequences {

    private SubSequences() {
    }

    public static List<Double> fuzzyLongestCommonSubSequences(List<String> a, List<String> b, StringDistance d, double threshold) {
        int[][] lengths = new int[a.size()][b.size()];
        double[][] scores = new double[a.size()][b.size()];
        boolean[][] processed = new boolean[a.size()][b.size()];
        List<Double> ret = new ArrayList<>();
        double z = 0;
        if (a != null && b != null) {
            for (int i = 0; i < a.size(); i++) {
                for (int j = 0; j < b.size(); j++) {
                    double score = d.score(d.prepare(a.get(i)), d.prepare(b.get(j)));
                    scores[i][j] = score;
                    if (d != null && score > threshold) {
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

        int x = a.size() - 1;
        int y = b.size() - 1;
        double currentSequence = 0;
        while (x >= 0 && y >= 0) {
            /*The case where the is a single word match at the beginning of the strings*/
            if (((lengths[x][y] == 1 && (x == 0 || y == 0)) && !processed[x][y])) {
                ret.add(scores[x][y]);
                processed[x][y] = true;
            } else {
                int prevX = (x == 0) ? 0 : (x - 1);
                int prevY = (y == 0) ? 0 : y - 1;
                double lendiff = lengths[prevX][prevY] - (lengths[x][y] - 1);
                if ((lendiff < 0.001d && lendiff > -0.001d) && !processed[x][y]) {
                    currentSequence += scores[x][y];
                    int nx = x;
                    int ny = y;
                    while (!(nx <= 0 || ny <= 0 || lengths[nx][ny] == 0)) {
                        processed[nx][ny] = true;
                        ny--;
                        nx--;
                        currentSequence += scores[nx][ny];
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


    public static List<Double> longestCommonSubSequences(List<String> a, List<String> b) {
        int[][] lengths = new int[a.size()][b.size()];
        boolean[][] processed = new boolean[a.size()][b.size()];
        List<Double> ret = new ArrayList<>();
        double z = 0;
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (a.get(i).equals(b.get(j))) {
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

        int x = a.size() - 1;
        int y = b.size() - 1;
        double currentSequence = 0;
        while (x > 0 && y > 0) {
            if (lengths[x - 1][y - 1] == lengths[x][y] - 1 && !processed[x][y]) {
                currentSequence += 1;
                int nx = x;
                int ny = y;
                while (!(nx <= 0 || ny <= 0 || lengths[nx][ny] == 0)) {
                    processed[nx][ny] = true;
                    ny--;
                    nx--;
                    currentSequence += 1;
                }
                ret.add(currentSequence);
                currentSequence = 0;
            }
            y--;
            if (y == 0) {
                x--;
                y = b.size() - 1;
            }
        }
        return ret;
    }

    public static int longestSubString(String first, String second) {
        if (first == null || second == null || first.isEmpty() || second.isEmpty()) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];
        Iterable<Integer> cpFirst = new CodePointWrapper(first);
        int i = 0;
        for (int cpi : cpFirst) {
            Iterable<Integer> cpSecond = new CodePointWrapper(second);
            int j = 0;
            for (int cpj : cpSecond) {
                if (cpi == cpj) {
                    if (i == 0 || j == 0) {
                        table[i][j] = 1;
                    } else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
                j++;
            }
            i++;
        }
        return maxLen;
    }

}
