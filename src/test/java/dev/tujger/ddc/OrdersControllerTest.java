package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;

@SuppressWarnings({"deprecation", "FieldCanBeLocal"})
public class OrdersControllerTest {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String inputFileName = "./src/main/resources/input-test1.txt";
    private OrdersFromFile orders;
    private OrdersControllerLiveList deliveryController;
    private Order order;

    @Before
    public void setUp() throws Exception {
        orders = new OrdersFromFile(inputFileName);
        orders.update();
        deliveryController = new OrdersControllerLiveList();
        deliveryController.setOrders(orders);

        order = deliveryController.fetchNextOrder(LocalTime.of(7,30,0));
    }

    @Test
    public void estimateRequiredTimes() throws Exception {
        orders.update();
        deliveryController.estimateRequiredTimes();
    }

    @Test
    public void perform() throws IOException {
        orders.update();
        assertEquals(-100, deliveryController.fetchNPS());
        deliveryController.perform();
        assertTrue(deliveryController.fetchNPS() > 0);
    }

    @Test
    public void fetchNextOrder() {
        Date now = new Date();
        assertEquals("WM0002", deliveryController.fetchNextOrder(LocalTime.of(6,30,0)).getId());
        assertEquals("WM0008", deliveryController.fetchNextOrder(LocalTime.of(12,0,0)).getId());
    }

    @Test
    public void startOfDay() {
        LocalTime time = LocalTime.of(6,0,0);
        assertEquals(time, OrdersController.startOfDay());
    }

    @Test
    public void endOfDay() {
        LocalTime time = LocalTime.of(22,0,0);
        assertEquals(time, OrdersController.endOfDay());
    }

    @Test
    public void fetchNPS() {
        assertEquals(-100, deliveryController.fetchNPS());
    }

    @Test
    public void getOrderedList() {
        assertEquals(new LinkedHashSet<>(), deliveryController.getOrderedList());
    }

    @Test
    public void positiveCompletion() {
        assertEquals("09:10:10", deliveryController.positiveCompletion(order).format(formatter));
    }

    @Test
    public void neutralCompletion() {
        assertEquals("11:10:10", deliveryController.neutralCompletion(order).format(formatter));
    }
}