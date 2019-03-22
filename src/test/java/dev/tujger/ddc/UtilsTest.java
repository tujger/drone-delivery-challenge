package dev.tujger.ddc;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void print() {
        Utils.print("test");
    }

    @Test
    public void println() {
        Utils.println("test");
        Utils.println("test", -1);
        Utils.println("test", 1);
    }

}