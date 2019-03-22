package dev.tujger.ddc;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            String inputFileName = args[0];

            DroneDeliveryChallenge ddc = new DroneDeliveryChallenge();
            ddc.setOrders(new OrdersFromFile(inputFileName));
            if(args.length > 1) {
                String outputFileName = args[1];
                ddc.setOutputFileName(outputFileName);
            }

//            ddc.setOrdersController(new OrdersControllerLiveList());
            ddc.setOrdersController(new OrdersControllerPredefinedList());
            ddc.start();
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Required input and output file names as an arguments.");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(String.format("Error found: %s", e.getMessage()));
            e.printStackTrace();
        }
    }
}
