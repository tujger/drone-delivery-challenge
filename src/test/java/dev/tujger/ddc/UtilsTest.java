package dev.tujger.ddc;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
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

    @Test
    public void formatTime() {
        assertEquals("22:13:01", Utils.formatTime(new Date(2019,3,21,22,13,1)));
    }

    @Test
    public void modifyTime() {
        Date date = new Date(2019,3,21,22,14,1);
        assertEquals(date, Utils.modifyTime(new Date(2019,3,21,22,13,1), 60));
    }

}