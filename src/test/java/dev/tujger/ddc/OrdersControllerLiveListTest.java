package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"FieldCanBeLocal"})
public class OrdersControllerLiveListTest {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String inputFileName = "./src/main/resources/input-test1.txt";
    private OrdersFromFile orders;
    private OrdersControllerLiveList deliveryController;
    private LocalTime time;
    private Order order;

    @Before
    public void setUp() throws Exception {
        orders = new OrdersFromFile(inputFileName);
        orders.update();
        deliveryController = new OrdersControllerLiveList();
        deliveryController.setOrders(orders);

        time = LocalTime.of(7,30,0);

        order = deliveryController.fetchNextOrder(time);
    }

    @Test
    public void fetchNextOrder() {
        Order order = deliveryController.fetchNextOrder(time);
        assertEquals("WM0005", order.getId());
    }

    @Test
    public void optimalDepartureTime() {
        assertEquals("07:30:00", deliveryController.optimalDepartureTime(time, order).format(formatter));
    }
}