package dev.tujger.ddc;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

abstract public class DeliveryController {

    private Orders orders;
    private Set<Order> orderedList = new LinkedHashSet<>();

    public void estimateRequiredTimes() throws Exception {
        getOrders().update();
        int totalRequiredTime = 0;
        for(Order order: getOrders()) {
            int requiredTime = order.getDistance() * 2;
            totalRequiredTime += requiredTime;
        }
        System.out.println("====================================================================");
        System.out.println(String.format("Total time required: %02.0f:%02d:00",
                Math.floor(totalRequiredTime / 60.),
                totalRequiredTime % 60));
    }

    public static String formatTimestamp(Date date) {
        return String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
    }

    public static Date modifyTime(Date date, int secondsToAdd) {
        long time = date.getTime();
        time += secondsToAdd * 1000;
        return new Date(time);
    }

    abstract public Order fetchNextOrder(Date timestamp);

    public void perform() {
        try {
            getOrders().update();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Date timestamp = startOfDay();
        System.out.println(String.format("\nStarting delivery at %s", DeliveryController.formatTimestamp(timestamp)));
        System.out.println("====================================================================");
        while(timestamp.before(endOfDay())) {
            Order order = fetchNextOrder(timestamp);
            if(order == null) {
                System.out.println(String.format("No orders at %s, skipping 15 minutes",
                        DeliveryController.formatTimestamp(timestamp)));
                timestamp = DeliveryController.modifyTime(timestamp, 15 * 60);
                try {
                    getOrders().update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            Date newTimestamp = optimalDepartureTime(timestamp, order);
            if(newTimestamp.after(timestamp)) {
                long interval = (newTimestamp.getTime() - timestamp.getTime())/1000;
                System.out.println(String.format("Empty time, skipping %02d:%02d:%02d",
                        interval / 60 / 60,
                        (interval - interval / 60 / 60 * 60 * 60) / 60,
                        interval % 60));
                timestamp = newTimestamp;
            }
            System.out.println(String.format("Order %s, order time %s, positive window %s-%s, neutral window %s-%s, distance %d",
                    order.getId(),
                    DeliveryController.formatTimestamp(order.getTimestamp()),
                    DeliveryController.formatTimestamp(leftPositive(order)),
                    DeliveryController.formatTimestamp(rightPositive(order)),
                    DeliveryController.formatTimestamp(leftNeutral(order)),
                    DeliveryController.formatTimestamp(rightNeutral(order)),
                    order.getDistance()));
            order.setDepartureTime(timestamp);
            System.out.println(String.format("- departed at\t\t%s",
                    DeliveryController.formatTimestamp(timestamp)));

            timestamp = DeliveryController.modifyTime(timestamp, order.getDistance() * 60);
            order.setCompletionTime(timestamp);

            if(timestamp.before(rightPositive(order)) && timestamp.after(leftPositive(order))) {
                order.setFeedback(Feedback.Promote);
            } else if(timestamp.before(rightNeutral(order)) && timestamp.after(leftNeutral(order))) {
                order.setFeedback(Feedback.Neutral);
            } else {
                order.setFeedback(Feedback.Detract);
            }

            System.out.println(String.format("- delivered at\t\t%s, %s",
                    DeliveryController.formatTimestamp(timestamp),
                    order.getFeedback()));
            timestamp = DeliveryController.modifyTime(timestamp, order.getDistance() * 60);
            System.out.println(String.format("- returned at\t\t%s",
                    DeliveryController.formatTimestamp(timestamp)));
            getOrderedList().add(order);
        }
    }


    public static Date startOfDay() {
        Date timestamp = new Date();
        timestamp = new Date(timestamp.getYear(), timestamp.getMonth(), timestamp.getDate(), 6, 0, 0);
        return timestamp;
    }

    public static Date endOfDay() {
        Date timestamp = new Date(startOfDay().getTime());
        timestamp.setHours(22);
        return timestamp;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Orders getOrders() {
        return orders;
    }

    public int fetchNPS() {
        int positive = 0;
        int negative = 0;
        for(Order order: getOrders()) {
            if(Feedback.Promote.equals(order.getFeedback())) {
                positive++;
            } else if(Feedback.Detract.equals(order.getFeedback())) {
                negative++;
            } else if(order.getFeedback() == null) {
                negative++;
            }
        }
        float promoters = 1F * positive / getOrders().size();
        float detractors = 1F * negative / getOrders().size();
        return Math.round((promoters - detractors) * 100);
    }

    public Set<Order> getOrderedList() {
        return orderedList;
    }

    public abstract Date leftPositive(Order order);

    public abstract Date leftNeutral(Order order);

    public abstract Date rightPositive(Order order);

    public abstract Date rightNeutral(Order order);

    public abstract Date optimalDepartureTime(Date currentTimestamp, Order order);
}
