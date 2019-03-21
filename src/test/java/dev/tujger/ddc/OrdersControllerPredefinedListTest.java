package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"deprecation", "FieldCanBeLocal"})
public class OrdersControllerPredefinedListTest {

    private final String inputFileName = "./src/main/resources/input-test1.txt";
    private OrdersFromFile orders;
    private OrdersControllerLiveList deliveryController;
    private Date date;
    private Order order;


    @Before
    public void setUp() throws Exception {
        orders = new OrdersFromFile(inputFileName);
        orders.update();

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;
        OrdersControllerPredefinedList.IN_ADVANCE_MAX = 8;
        OrdersControllerPredefinedList.IN_ADVANCE_RECALCULATE = 2;

        deliveryController = new OrdersControllerPredefinedList();
        deliveryController.setOrders(orders);

        date = new Date();
        date = new Date(date.getYear(), date.getMonth(), date.getDate(), 7, 30,0);

        order = deliveryController.fetchNextOrder(date);
    }

    @Test
    public void fetchNextOrder() {
        Order order = deliveryController.fetchNextOrder(date);
        assertEquals("WM0002", order.getId());
    }

    @Test
    public void leftPositive() {
        assertEquals("06:00:00", OrdersController.formatTime(deliveryController.leftPositive(order)));
    }

    @Test
    public void leftNeutral() {
        assertEquals("06:00:00", OrdersController.formatTime(deliveryController.leftNeutral(order)));
    }

    @Test
    public void rightPositive() {
        assertEquals("07:11:50", OrdersController.formatTime(deliveryController.rightPositive(order)));
    }

    @Test
    public void rightNeutral() {
        assertEquals("09:11:50", OrdersController.formatTime(deliveryController.rightNeutral(order)));
    }

    @Test
    public void optimalDepartureTime() {
        assertEquals("07:30:00", OrdersController.formatTime(deliveryController.optimalDepartureTime(date, order)));
    }
}