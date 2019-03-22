package dev.tujger.ddc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("FieldCanBeLocal")
public class DroneDeliveryChallengeTest {

    private final String inputFileName1 = "./src/main/resources/input-test1.txt";
    private final String inputFileName2 = "./src/main/resources/input-test2.txt";
    private final String outputFileName = "./src/main/resources/output-test.txt";

    private DroneDeliveryChallenge ddc;

    @Before
    public void setUp() throws Exception {
        OrdersControllerPredefinedList.IN_ADVANCE_MAX = 8;
        OrdersControllerPredefinedList.IN_ADVANCE_RECALCULATE = 2;

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;

        ddc = new DroneDeliveryChallenge();
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.setOutputFileName(outputFileName);
        ddc.setOrdersController(new OrdersControllerLiveList());
    }

    @Test
    public void start() throws Exception {

        ddc.start();
        assertEquals(72, ddc.getOrdersController().fetchNPS());

        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(67, ddc.getOrdersController().fetchNPS());

        ddc.setOrdersController(new OrdersControllerPredefinedList());

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = true;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(21, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = true;
        OrdersControllerPredefinedList.FEE_DISTANCE = false;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(21, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(67, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = false;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(67, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = false;
        OrdersControllerPredefinedList.FEE_COMPARING = true;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(-87, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = false;
        OrdersControllerPredefinedList.FEE_COMPARING = true;
        OrdersControllerPredefinedList.FEE_DISTANCE = false;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(-87, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = false;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(-12, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = false;
        OrdersControllerPredefinedList.FEE_COMPARING = false;
        OrdersControllerPredefinedList.FEE_DISTANCE = false;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(21, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.FEE_LATE = true;
        OrdersControllerPredefinedList.FEE_COMPARING = true;
        OrdersControllerPredefinedList.FEE_DISTANCE = true;

        OrdersControllerPredefinedList.IN_ADVANCE_MAX = 8;
        OrdersControllerPredefinedList.IN_ADVANCE_RECALCULATE = 1;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(29, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.IN_ADVANCE_MAX = 6;
        OrdersControllerPredefinedList.IN_ADVANCE_RECALCULATE = 2;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(50, ddc.getOrdersController().fetchNPS());

        OrdersControllerPredefinedList.IN_ADVANCE_MAX = 3;
        OrdersControllerPredefinedList.IN_ADVANCE_RECALCULATE = 1;
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.getOrders().update();
        ddc.getOrders().setSource(inputFileName2);
        ddc.getOrders().update();
        ddc.start();
        assertEquals(25, ddc.getOrdersController().fetchNPS());
    }

    @Test
    public void getOutputFileName() {
        assertEquals(outputFileName, ddc.getOutputFileName());
    }

    @Test
    public void setOutputFileName() {
        ddc.setOutputFileName("not-exists");
        assertEquals("not-exists", ddc.getOutputFileName());
    }

    @Test
    public void getDeliveryController() throws Exception {
        ddc.start();
        assertEquals(72, ddc.getOrdersController().fetchNPS());
    }

    @Test
    public void setDeliveryController() throws Exception {
        ddc.start();
        assertEquals(72, ddc.getOrdersController().fetchNPS());
        ddc.setOrdersController(new OrdersControllerPredefinedList());
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        ddc.start();
        assertEquals(78, ddc.getOrdersController().fetchNPS());
    }

    @Test
    public void getOrders() throws Exception {
        ddc.start();
        assertEquals(18, ddc.getOrders().size());
    }

    @Test
    public void setOrders() throws Exception {
        ddc.start();
        ddc.setOrders(new OrdersFromFile(inputFileName1));
        assertEquals(0, ddc.getOrders().size());
    }
}