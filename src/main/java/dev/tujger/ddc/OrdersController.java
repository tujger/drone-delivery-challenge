package dev.tujger.ddc;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "deprecation"})
abstract public class OrdersController {

    private Orders orders;
    private Set<Order> orderedList = new LinkedHashSet<>();

    public void estimateRequiredTimes() throws Exception {
        getOrders().update();
        int totalRequiredTime = 0;
        for(Order order: getOrders()) {
            int requiredTime = order.getDistance() * 2;
            totalRequiredTime += requiredTime;
        }
        Utils.println(String.format("Total time required: %02.0f:%02d:00",
                Math.floor(totalRequiredTime / 60.),
                totalRequiredTime % 60), -1);
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

        Utils.println(String.format("\nStarting delivery at %s", Utils.formatTime(timestamp)), 1);

        while(timestamp.before(endOfDay())) {
            Order order = fetchNextOrder(timestamp);
            if(order == null) {
                Utils.println(String.format("No valid orders at %s, skipping 15 minutes",
                        Utils.formatTime(timestamp)));
                timestamp = Utils.modifyTime(timestamp, 15 * 60);
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
                Utils.println(String.format("Empty time, skipping %02d:%02d:%02d",
                        interval / 60 / 60,
                        (interval - interval / 60 / 60 * 60 * 60) / 60,
                        interval % 60));
                timestamp = newTimestamp;
            }
            Utils.println(String.format("Order %s, coordinates %s, distance %d\n" +
                                                "- order time\t\t%s\n" +
                                                "- positive before\t%s\n" +
                                                "- neutral before\t%s\n" +
                                                "- departed at\t\t%s",
                    order.getId(), order.getCoordinate(), order.getDistance(),
                    Utils.formatTime(order.getTimestamp()),
                    Utils.formatTime(positiveCompletion(order)),
                    Utils.formatTime(neutralCompletion(order)),
                    Utils.formatTime(timestamp)));
            order.setDepartureTime(timestamp);

            timestamp = Utils.modifyTime(timestamp, order.getDistance() * 60);
            order.setCompletionTime(timestamp);

            if(timestamp.before(positiveCompletion(order))) {
                order.setFeedback(Feedback.Promote);
            } else if(timestamp.before(neutralCompletion(order))) {
                order.setFeedback(Feedback.Neutral);
            } else {
                order.setFeedback(Feedback.Detract);
            }

            Utils.println(String.format("- delivered at\t\t%s, %s",
                    Utils.formatTime(timestamp),
                    order.getFeedback()));
            timestamp = Utils.modifyTime(timestamp, order.getDistance() * 60);
            Utils.println(String.format("- returned at\t\t%s",
                    Utils.formatTime(timestamp)));

            getOrderedList().add(order);
        }
    }

    public static Date startOfDay() {
        Date timestamp = new Date();
        // reinitialize timestamp to dismiss milliseconds effect
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

    public Date positiveCompletion(Order order) {
        Date desired = Utils.modifyTime(order.getTimestamp(), 120 * 60);
        Date limit = endOfDay();
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    public Date neutralCompletion(Order order) {
        Date desired = Utils.modifyTime(order.getTimestamp(), 240 * 60);
        Date limit = endOfDay();
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    public abstract Date optimalDepartureTime(Date currentTimestamp, Order order);
}
