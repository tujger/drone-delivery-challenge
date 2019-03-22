package dev.tujger.ddc;

import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("WeakerAccess")
class DroneDeliveryChallenge {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private OrdersController ordersController;
    private Orders orders;
    private String outputFileName;

    public void start() throws Exception {
        getOrdersController().setOrders(getOrders());
        getOrdersController().estimateRequiredTimes();
        getOrdersController().perform();

        Utils.println("\r                                                           ");
        Utils.println("Summary:", 1);

        if(getOutputFileName() == null) {
            setOutputFileName(new File(".", "result.txt").getCanonicalPath());
        }

        File file = new File(getOutputFileName());
        boolean directoryCreated = file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(getOutputFileName())) {
            for (Order order : getOrdersController().getOrderedList()) {
                Utils.println(order.toString());
                writer.write(String.format("%s %s\n", order.getId(), order.getDepartureTime().format(formatter)));
            }
            Orders notDelivered = new OrdersFromFile(getOrders());
            notDelivered.removeAll(getOrdersController().getOrderedList());
            if (!notDelivered.isEmpty()) {
                Utils.println("\nOrders not delivered, Detract:", 1);
                for (Order order : notDelivered) {
                    Utils.println(order.toString());
                }
            }
            Utils.println("", 1);
            Utils.println(String.format("NPS: %d", getOrdersController().fetchNPS()));
            writer.write(String.format("NPS %s\n", getOrdersController().fetchNPS()));
        }
        if(directoryCreated) Utils.println(String.format("Directory created: %s", file.getParentFile().getCanonicalPath()));
        System.out.println(String.format("Output file: %s", file.getCanonicalPath()));
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public OrdersController getOrdersController() {
        return ordersController;
    }

    public void setOrdersController(OrdersController ordersController) {
        this.ordersController = ordersController;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
}