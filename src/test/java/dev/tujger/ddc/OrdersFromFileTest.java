package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrdersFromFileTest {

    private Orders orders;
    private String inputFileName1 = "./src/main/resources/input-test1.txt";
    @SuppressWarnings("FieldCanBeLocal")
    private String inputFileName2 = "./src/main/resources/input-test2.txt";

    @Before
    public void setUp() throws Exception {
        orders = new OrdersFromFile(inputFileName1);
    }

    @Test
    public void update() throws Exception {
        orders.update();
        assertEquals(18, orders.size());

        orders.update();
        assertEquals(18, orders.size());

        Order order = orders.get(0);
        assertEquals("WM0001", order.getId());
        assertEquals(16, order.getDistance());

        orders.setSource(inputFileName2);
        orders.update();
        assertEquals(24, orders.size());

        orders.update();
        assertEquals(24, orders.size());

        order = orders.get(19);
        assertEquals("UM0020", order.getId());
        assertEquals(13, order.getDistance());

        orders.setSource(inputFileName1 + "-not-exists");
        orders.update();

        assertEquals(24, orders.size());
    }

    @Test
    public void setSource() {
        orders.setSource("new/source");
        assertEquals("new/source", orders.getSource());
    }

    @Test
    public void getSource() {
        assertEquals(inputFileName1, orders.getSource());
    }

    @Test
    public void secondConstructor() throws Exception {
        orders.update();
        OrdersFromFile orders2 = new OrdersFromFile(orders);
        assertEquals(18, orders.size());
        assertEquals(orders.size(), orders2.size());
    }
}