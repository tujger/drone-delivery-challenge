package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OrderTest {

    private Order order;

    @Before
    public void setUp() {
        String line = "WM0003 N7E50 05:31:50";
        order = new Order(line);
    }

    @Test
    public void getId() {
        assertEquals("WM0003", order.getId());
    }

    @Test
    public void setId() {
        order.setId("WM0004");
        assertEquals("WM0004", order.getId());
    }

    @Test
    public void getCoordinate() {
        assertEquals("N7E50", order.getCoordinate());
    }

    @Test
    public void setCoordinate() {
        order.setCoordinate("W20S20");
        assertEquals("W20S20", order.getCoordinate());
    }

    @Test
    public void getTimestamp() {
        assertEquals(5, order.getTimestamp().getHour());
        assertEquals(31, order.getTimestamp().getMinute());
        assertEquals(50, order.getTimestamp().getSecond());
    }

    @Test
    public void setTimestamp() {
        LocalTime timestamp = LocalTime.of(13,45,10);
        order.setTimestamp(timestamp);
        assertEquals(timestamp, order.getTimestamp());  }

    @Test
    public void getDistance() {
        assertEquals(57, order.getDistance());
    }

    @Test
    public void setDistance() {
        order.setDistance(100);
        assertEquals(100, order.getDistance());    }

    @Test
    public void getFeedback() {
        assertNull(order.getFeedback());
    }

    @Test
    public void setFeedback() {
        order.setFeedback(Feedback.Promote);
        assertEquals(Feedback.Promote, order.getFeedback());
        order.setFeedback(Feedback.Detract);
        assertEquals(Feedback.Detract, order.getFeedback());
    }

    @Test
    public void getDepartureTime() {
        assertNull(order.getDepartureTime());
    }

    @Test
    public void setDepartureTime() {
        LocalTime timestamp = LocalTime.of(11,11,11);
        order.setDepartureTime(timestamp);
        assertEquals(timestamp, order.getDepartureTime());
    }

    @Test
    public void getCompletionTime() {
        assertNull(order.getCompletionTime());
    }

    @Test
    public void setCompletionTime() {
        LocalTime timestamp = LocalTime.of(22,22,22);
        order.setCompletionTime(timestamp);
        assertEquals(timestamp, order.getCompletionTime());   }

    @Test
    public void toStringTest() {
        order.setDepartureTime(LocalTime.of(10,11,12));
        order.setCompletionTime(LocalTime.of(13,14,15));
        order.setFeedback(Feedback.Promote);
        System.out.println(order);
    }
}