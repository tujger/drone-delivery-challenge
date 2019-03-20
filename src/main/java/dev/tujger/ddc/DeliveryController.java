package dev.tujger.ddc;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

abstract public class DeliveryController {

    private Orders orders;
    private List<Order> orderedList = new LinkedList<>();

    public void estimateRequiredTimes() throws Exception {
        getOrders().update();
        int totalRequiredTime = 0;
        for(Order order: getOrders()) {
            int requiredTime = order.getDistance() * 2;
            totalRequiredTime += requiredTime;
        }
        System.out.println(String.format("Total time required: %.0f:%d", Math.floor(totalRequiredTime / 60.), totalRequiredTime % 60));
    }

    public static String formatTimestamp(Date date) {
        return String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
    }

    public static Date modifyTime(Date date, int secondsToAdd) {
        long time = date.getTime();
        time += secondsToAdd * 1000;
        return new Date(time);
    }

    abstract public List<Order> fetchAvailableOrders(Date timestamp);

    public void perform() {
        try {
            getOrders().update();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Date timestamp = startOfDay();
        System.out.println(String.format("Starting delivery at %s", timestamp));
        while(timestamp.before(endOfDay())) {
            List<Order> availableOrders = fetchAvailableOrders(timestamp);
            if(availableOrders.size() == 0) {
                System.out.println(String.format("No orders at %s, skipping 15 minutes", DeliveryController.formatTimestamp(timestamp)));
                timestamp = DeliveryController.modifyTime(timestamp, 15 * 60);
                try {
                    getOrders().update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            for(Order order: availableOrders) {
                if(DeliveryController.modifyTime(timestamp, +order.getDistance() * 60).after(order.getTimestamp())) {
                } else {
                    timestamp = startDeliveryTimestamp(timestamp, order);
                }
                System.out.println(String.format("Order %s, ordered for %s, positive window %s-%s, distance %d", order.getId(), DeliveryController.formatTimestamp(order.getTimestamp()), DeliveryController.formatTimestamp(leftPositive(order)), DeliveryController.formatTimestamp(rightPositive(order)), order.getDistance()));
                order.started(timestamp);
                System.out.println(String.format("--- started at\t\t%s", DeliveryController.formatTimestamp(timestamp)));

                timestamp = DeliveryController.modifyTime(timestamp, order.getDistance() * 60);
                order.setDeliveredTime(timestamp);

                if(timestamp.before(rightPositive(order)) && timestamp.after(leftPositive(order))) {
                    order.setFeedback(Feedback.Positive);
                } else if(timestamp.before(rightNeutral(order)) && timestamp.after(leftNeutral(order))) {
                    order.setFeedback(Feedback.Neutral);
                } else {
                    order.setFeedback(Feedback.Negative);
                }

                System.out.println(String.format("--- delivered at\t%s with %s feedback", DeliveryController.formatTimestamp(timestamp), order.getFeedback()));
                timestamp = DeliveryController.modifyTime(timestamp, order.getDistance() * 60);
                System.out.println(String.format("--- returned at\t\t%s", DeliveryController.formatTimestamp(timestamp)));
                getOrderedList().add(order);
            }
        }
    }


    public static Date startOfDay() {
        Date timestamp = new Date();
        timestamp.setHours(6);
        timestamp.setMinutes(0);
        timestamp.setSeconds(0);
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
            if(Feedback.Positive.equals(order.getFeedback())) {
                positive++;
            } else if(Feedback.Negative.equals(order.getFeedback())) {
                negative++;
            }
        }
        float promoters = 1F * positive / getOrders().size();
        float detractors = 1F * negative / getOrders().size();
        return Math.round((promoters - detractors) * 100);
    }

    public List<Order> getOrderedList() {
        return orderedList;
    }

    public abstract Date leftPositive(Order order);

    public abstract Date leftNeutral(Order order);

    public abstract Date rightPositive(Order order);

    public abstract Date rightNeutral(Order order);

    public abstract Date startDeliveryTimestamp(Date currentTimestamp, Order order);
}
