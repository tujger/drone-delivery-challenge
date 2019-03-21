package dev.tujger.ddc;

import java.io.File;
import java.io.FileWriter;

class DroneDeliveryChallenge {
    private DeliveryController deliveryController;
    private Orders orders;
    private String outputFileName;

    public void start() throws Exception {

        deliveryController.setOrders(orders);
        deliveryController.estimateRequiredTimes();
        deliveryController.perform();

        System.out.println("\nSummary:");
        System.out.println("====================================================================");
        File file = new File(getOutputFileName());
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(getOutputFileName())) {
            for(Order order: deliveryController.getOrderedList()) {
                System.out.println(order);
                writer.write(String.format("%s %s\n", order.getId(), DeliveryController.formatTimestamp(order.getDepartureTime())));
            }
            Orders notDelivered = new OrdersFromFile(orders);
            notDelivered.removeAll(deliveryController.getOrderedList());
            if(!notDelivered.isEmpty()) {
                System.out.println("\nOrders not delivered, Detract:");
                System.out.println("====================================================================");
                for (Order order : notDelivered) {
                    System.out.println(order);
                }
            }
            System.out.println("\n====================================================================");
            System.out.println(String.format("NPS: %d", deliveryController.fetchNPS()));
            writer.write(String.format("NPS %s\n", deliveryController.fetchNPS()));
        }
        System.out.println(String.format("Output file name: %s", file.getCanonicalPath()));
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public DeliveryController getDeliveryController() {
        return deliveryController;
    }

    public void setDeliveryController(DeliveryController deliveryController) {
        this.deliveryController = deliveryController;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
}
