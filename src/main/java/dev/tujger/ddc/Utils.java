package dev.tujger.ddc;

import java.util.Date;

@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Utils {

    private static final Boolean SHOW_LOG = true;

    public static void print(String text) {
        if(!SHOW_LOG) return;
        System.out.print(text);
    }

    public static void println(String text) {
        println(text, 0);
    }

    public static void println(String text, int separator) {
        if(!SHOW_LOG) return;
        if(separator < 0) {
            System.out.println("====================================================================");
        }
        if(text != null) {
            System.out.println(text);
        }
        if(separator > 0) {
            System.out.println("====================================================================");
        }
    }
}
