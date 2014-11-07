package org.getalp.lexsema.util;

import java.util.Scanner;

/**
 * Created by tchechem on 10/29/14.
 */
public class VisualVMTools {
    public static void delayUntilReturn() {
        Scanner s = new Scanner(System.in);
        System.err.println("Press Enter to continue...");
        s.nextLine();
    }
}
