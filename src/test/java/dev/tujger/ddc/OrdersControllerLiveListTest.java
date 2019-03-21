package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

@SuppressWarnings({"deprecation", "FieldCanBeLocal"})
public class OrdersControllerLiveListTest {

    private final String inputFileName = "./src/main/resources/input-test1.txt";
    private OrdersFromFile orders;
    private OrdersControllerLiveList deliveryController;
    private Date date;
    private Order order;


    @Before
    public void setUp() throws Exception {
        orders = new OrdersFromFile(inputFileName);
        orders.update();
        deliveryController = new OrdersControllerLiveList();
        deliveryController.setOrders(orders);

        date = new Date();
        date = new Date(date.getYear(), date.getMonth(), date.getDate(), 7, 30,0);

        order = deliveryController.fetchNextOrder(date);
    }

    @Test
    public void fetchNextOrder() {
        Order order = deliveryController.fetchNextOrder(date);
        assertEquals("WM0005", order.getId());
    }

    @Test
    public void leftPositive() {
        assertEquals("07:10:10", OrdersController.formatTime(deliveryController.leftPositive(order)));
    }

    @Test
    public void leftNeutral() {
        assertEquals("07:10:10", OrdersController.formatTime(deliveryController.leftNeutral(order)));
    }

    @Test
    public void rightPositive() {
        assertEquals("09:10:10", OrdersController.formatTime(deliveryController.rightPositive(order)));
    }

    @Test
    public void rightNeutral() {
        assertEquals("11:10:10", OrdersController.formatTime(deliveryController.rightNeutral(order)));
    }

    @Test
    public void optimalDepartureTime() {
        assertEquals("07:30:00", OrdersController.formatTime(deliveryController.optimalDepartureTime(date, order)));
    }
}