package org.getalp.util;

import com.wcohen.ss.AbstractStringDistance;
import org.getalp.encoding.CodePointWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 10/13/14.
 */
public class SubSequences {
    public static List<Double> fuzzyLongestCommonSubSequences(List<String> a, List<String> b, AbstractStringDistance d, double threshold) {
        int[][] lengths = new int[a.size()][b.size()];
        double[][] scores = new double[a.size()][b.size()];
        boolean[][] processed = new boolean[a.size()][b.size()];
        List<Double> ret = new ArrayList<Double>();
        double z = 0;
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                double score = 0;
                d.score(d.prepare(a.get(i)), d.prepare(b.get(j)));
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

       /* System.out.print("  ");
        for (int i = 0; i < b.size(); i++) {
            System.out.print(b.get(i) + " ");
        }
        System.out.println();
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + " ");
            for (int j = 0; j < b.size(); j++) {
                System.out.print(lengths[i][j] + " ");
            }
            System.out.println();
        }

        System.out.print("  ");
        for (int i = 0; i < b.size(); i++) {
            System.out.print(b.get(i) + "   ");
        }
        System.out.println();
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + " ");
            for (int j = 0; j < b.size(); j++) {
                System.out.print(scores[i][j] + " ");
            }
            System.out.println();
        }*/

        int x = a.size() - 1;
        int y = b.size() - 1;
        double currentSequence = 0;
        while (x > 0 && y > 0) {
            double lendiff = lengths[x - 1][y - 1] - (lengths[x][y] - 1);
            if (lendiff < 0.001d && lendiff > -0.001d && !processed[x][y]) {
                currentSequence += scores[x][y];
                int nx = x;
                int ny = y;
                while (!(ny <= 0 || lengths[nx][ny] == 0)) {
                    processed[nx][ny]=true;
                    ny--;
                    nx--;
                    currentSequence += scores[nx][ny];
                }
                ret.add((double) currentSequence);
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
        if (first == null || second == null || first.length() == 0 || second.length() == 0) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];
        CodePointWrapper cpFirst = new CodePointWrapper(first);
        int i = 0;
        for (int cpi : cpFirst) {
            CodePointWrapper cpSecond = new CodePointWrapper(second);
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
