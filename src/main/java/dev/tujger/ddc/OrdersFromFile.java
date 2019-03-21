package dev.tujger.ddc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class OrdersFromFile extends ArrayList<Order> implements Orders {
    private List<String> ordersIds;
    private String source;

    public OrdersFromFile(String source) throws IOException {
        ordersIds = new ArrayList<>();
        setSource(source);
        File inputFile = new File(getSource());
        System.out.println(String.format("Input file: %s", inputFile.getCanonicalPath()));
    }

    public OrdersFromFile(Orders orders) {
        super(orders);
        ordersIds = new ArrayList<>();
        for(Order order:orders) {
            ordersIds.add(order.getId());
        }
    }

    public void update() throws IOException {
        File inputFile = new File(getSource());
        if(!inputFile.exists()) {
            System.err.println(String.format("File %s not found.", inputFile.getAbsolutePath()));
            return;
        }
        if(size() == 0) {
            Utils.println("\t\t\t ID\t\t\t\t\tCoordinate\t\tDistance\tStart", 1);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                try {
                    Order order = new Order(inputLine);
                    if(!ordersIds.contains(order.getId())) {
                        add(order);
                        ordersIds.add(order.getId());
                        Utils.println(String.format("Order added: %s", order));
                    }
                } catch (ParseException | ArrayIndexOutOfBoundsException e) {
                    System.err.println(String.format("Error parsing line: %s", inputLine));
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

}
