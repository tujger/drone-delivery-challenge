package dev.tujger.ddc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DroneDeliveryChallenge {
    private String inputFileName;
    private String outputFileName;
    private List<Order> orders;
    private TimeController timeController = new TimeController();

    public void start() throws Exception {
        System.out.println(String.format("Input file name: %s", getInputFileName()));

        File inputFile = new File(getInputFileName());
        if(!inputFile.exists()) {
            System.out.println(String.format("File %s not found.", inputFile.getAbsolutePath()));
            return;
        }
        orders = parseFile(inputFile);
        timeController.estimateRequiredTimes(orders);

        fullfillDelivery(orders);

        for(Order order:orders) {
            System.out.println(order);
        }
    }

    private void fullfillDelivery(List<Order> orders) {
        ArrayList<Order> list = new ArrayList<>(orders);
        Date timestamp = new Date();
        timestamp.setHours(6);
        timestamp.setMinutes(0);
        timestamp.setSeconds(0);

        System.out.println(String.format("Starting fulfillment at %s", timestamp));
        while(list.size() > 0) {
            Order order = list.remove(0);
            if(TimeController.add(timestamp, +order.getDistance()).after(order.getTimestamp())) {
            } else {
                timestamp = TimeController.add(timestamp, -order.getDistance());
            }
            System.out.println(String.format("Order %s, ordered for %s, positive window %s-%s, distance %d", order.getId(), TimeController.formatTimestamp(order.getTimestamp()), TimeController.formatTimestamp(order.leftPositive()), TimeController.formatTimestamp(order.rightPositive()), order.getDistance()));
            System.out.println(String.format("--- started at\t\t%s", TimeController.formatTimestamp(timestamp)));

            timestamp = TimeController.add(timestamp, order.getDistance());
            Feedback feedback = order.delivered(timestamp);
            System.out.println(String.format("--- delivered at\t%s with %s feedback", TimeController.formatTimestamp(timestamp), feedback));
            timestamp = TimeController.add(timestamp, order.getDistance());
            System.out.println(String.format("--- returned at\t\t%s", TimeController.formatTimestamp(timestamp)));
        }
    }

    private List<Order> parseFile(File inputFile) throws IOException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                try {
                    Order order = new Order(inputLine);
                    orders.add(order);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return orders;
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
