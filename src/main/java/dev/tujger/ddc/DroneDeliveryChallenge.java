package dev.tujger.ddc;

class DroneDeliveryChallenge {
    private String inputFileName;
    private String outputFileName;
    private DeliveryController deliveryController = new DeliveryControllerLiveList();
    private Orders orders = new OrdersFromFile();

    public void start() throws Exception {
        System.out.println(String.format("Input file name: %s", getInputFileName()));

        orders.setSource(inputFileName);

        deliveryController.setOrders(orders);
        deliveryController.estimateRequiredTimes();
        deliveryController.perform();

        for(Order order: deliveryController.getOrderedList()) {
            System.out.println(order);
        }
        System.out.println(String.format("NPS: %d", deliveryController.fetchNPS()));
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
