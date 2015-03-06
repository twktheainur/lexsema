package org.getalp.lexsema.util;

import java.util.Scanner;

<<<<<<< HEAD
public final class VisualVMTools {
    public static void delayUntilReturn() {
        Scanner s = new Scanner(System.in);
        //noinspection UseOfSystemOutOrSystemErr
        System.err.println("Press Enter to continue...");
        s.nextLine();
    }

    private VisualVMTools() {
    }
=======
/**
 * Created by tchechem on 10/29/14.
 */
public class VisualVMTools {
    public static void delayUntilReturn() {
        Scanner s = new Scanner(System.in);
        System.err.println("Press Enter to continue...");
        s.nextLine();
    }
>>>>>>> 3a3750340a3077643b79ee5d6a9dee67f9bcdbc9
}
