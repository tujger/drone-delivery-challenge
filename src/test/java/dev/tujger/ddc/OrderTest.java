package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OrderTest {

    private Order order;

    @Before
    public void setUp() throws Exception {
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

    @SuppressWarnings("deprecation")
    @Test
    public void getTimestamp() {
        assertEquals(5, order.getTimestamp().getHours());
        assertEquals(31, order.getTimestamp().getMinutes());
        assertEquals(50, order.getTimestamp().getSeconds());
    }

    @Test
    public void setTimestamp() {
        Date timestamp = new Date();
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
        Date timestamp = new Date();
        order.setDepartureTime(timestamp);
        assertEquals(timestamp, order.getDepartureTime());
    }

    @Test
    public void getCompletionTime() {
        assertNull(order.getCompletionTime());
    }

    @Test
    public void setCompletionTime() {
        Date timestamp = new Date();
        order.setCompletionTime(timestamp);
        assertEquals(timestamp, order.getCompletionTime());   }

    @Test
    public void toStringTest() {
        order.setDepartureTime(new Date());
        order.setCompletionTime(new Date());
        order.setFeedback(Feedback.Promote);
        System.out.println(order);
    }
}