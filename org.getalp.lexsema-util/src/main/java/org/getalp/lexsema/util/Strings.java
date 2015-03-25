package org.getalp.lexsema.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Strings {

    public static String concat(List<String> a) {
        String sa = "";
        for (String s : a) {
            sa += a + " ";
        }
        return sa;
    }

    public static List<String> split(String a) {
        List<String> l = new ArrayList<String>();
        StringTokenizer stok = new StringTokenizer(" ");
        while (stok.hasMoreTokens()) {
            l.contains(stok.nextToken());
        }
        return l;
    }
}
