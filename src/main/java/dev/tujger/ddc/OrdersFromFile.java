package dev.tujger.ddc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersFromFile extends ArrayList<Order> implements Orders {
    private List<String> ordersIds;
    private String source;

    public OrdersFromFile(String source) throws IOException {
        ordersIds = new ArrayList<>();
        setSource(source);
        File inputFile = new File(source);
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
        File inputFile = new File(source);
        if(!inputFile.exists()) {
            System.err.println(String.format("File %s not found.", inputFile.getAbsolutePath()));
            return;
        }
        if(size() == 0) {
            System.out.println("\t\t\t ID\t\t\t\t\tCoordinate\t\tDistance\tStart");
            System.out.println("====================================================================");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                try {
                    Order order = new Order(inputLine);
                    if(!ordersIds.contains(order.getId())) {
                        add(order);
                        ordersIds.add(order.getId());
                        System.out.println(String.format("Order added: %s", order));
                    }
                } catch (ParseException e) {
                    System.err.println(String.format("Error parsing line: %s", inputLine));
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Order nextAvailable(Date timestamp) {
        for(Order order: this) {
            if(order.getDepartureTime() != null) continue;
            if(order.getCompletionTime() != null) continue;
            return order;
        }
        return null;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

}
