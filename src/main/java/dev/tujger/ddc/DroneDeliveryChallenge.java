package dev.tujger.ddc;

import java.io.File;
import java.io.FileWriter;

@SuppressWarnings("WeakerAccess")
class DroneDeliveryChallenge {
    private OrdersController ordersController;
    private Orders orders;
    private String outputFileName;

    public void start() throws Exception {
        getOrdersController().setOrders(getOrders());
        getOrdersController().estimateRequiredTimes();
        getOrdersController().perform();

        System.out.println("\nSummary:");
        System.out.println("====================================================================");
        File file = new File(getOutputFileName());
        boolean directoryCreated = file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(getOutputFileName())) {
            for (Order order : getOrdersController().getOrderedList()) {
                System.out.println(order);
                writer.write(String.format("%s %s\n", order.getId(), OrdersController.formatTime(order.getDepartureTime())));
            }
            Orders notDelivered = new OrdersFromFile(getOrders());
            notDelivered.removeAll(getOrdersController().getOrderedList());
            if (!notDelivered.isEmpty()) {
                System.out.println("\nOrders not delivered, Detract:");
                System.out.println("====================================================================");
                for (Order order : notDelivered) {
                    System.out.println(order);
                }
            }
            System.out.println("\n====================================================================");
            System.out.println(String.format("NPS: %d", getOrdersController().fetchNPS()));
            writer.write(String.format("NPS %s\n", getOrdersController().fetchNPS()));
        }
        if(directoryCreated) System.out.println(String.format("Directory created: %s", file.getParentFile().getCanonicalPath()));
        System.out.println(String.format("Output file name: %s", file.getCanonicalPath()));
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