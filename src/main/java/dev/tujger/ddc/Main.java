package dev.tujger.ddc;

public class Main {
    public static void main(String[] args) {
        try {
            String inputFileName = args[0];
            String outputFileName = args[1];

            DroneDeliveryChallenge ddc = new DroneDeliveryChallenge();
            ddc.setOrders(new OrdersFromFile(inputFileName));
            ddc.setOutputFileName(outputFileName);

            /*
             * Uncomment the desired approach,
             *
             */
//            ddc.setOrdersController(new OrdersControllerLiveList());
            ddc.setOrdersController(new OrdersControllerPredefinedList());
            ddc.start();
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Required input and output file names as an arguments.");
        } catch (Exception e) {
            System.out.println(String.format("Error found: %s", e.getMessage()));
            e.printStackTrace();
        }
    }
}
