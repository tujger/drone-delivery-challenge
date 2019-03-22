package dev.tujger.ddc;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "deprecation"})
abstract public class OrdersController {

    private Orders orders;
    private Set<Order> orderedList = new LinkedHashSet<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void estimateRequiredTimes() throws Exception {
        getOrders().update();
        LocalTime totalRequiredTime = LocalTime.of(0,0,0);
        for(Order order: getOrders()) {
            totalRequiredTime = totalRequiredTime.plus(order.getDistance() * 2, ChronoUnit.MINUTES);
        }
        Utils.println(String.format("Total time required: %s",
                totalRequiredTime.format(formatter)));
    }

    abstract public Order fetchNextOrder(LocalTime timestamp);

    public void perform() {
        try {
            getOrders().update();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        LocalTime timestamp = startOfDay();

        Utils.println(String.format("\nStarting delivery at %s", timestamp.format(formatter)), 1);

        while(timestamp.isBefore(endOfDay())) {
            Order order = fetchNextOrder(timestamp);
            if(order == null) {
                Utils.print(String.format("No valid orders at %s, skipping minute", timestamp.format(formatter)));
                timestamp = timestamp.plus(1, ChronoUnit.MINUTES);
                if (timestamp.getMinute() % 15 == 0) {
                    try {
                        getOrders().update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }

            LocalTime newTimestamp = optimalDepartureTime(timestamp, order);
            if(newTimestamp.isAfter(timestamp)) {
                Duration duration = Duration.between(timestamp, newTimestamp);
                Utils.println(String.format("Empty time, skipping %s minutes", duration.toMinutes()));
                timestamp = newTimestamp;
            }
            Utils.println(String.format("Order %s, coordinates %s, distance %d\n" +
                                                "- order time\t\t%s\n" +
                                                "- positive before\t%s\n" +
                                                "- neutral before\t%s\n" +
                                                "- departed at\t\t%s",
                    order.getId(), order.getCoordinate(), order.getDistance(),
                    order.getTimestamp().format(formatter),
                    positiveCompletion(order).format(formatter),
                    neutralCompletion(order).format(formatter),
                    timestamp.format(formatter)));
            order.setDepartureTime(timestamp);

            timestamp = timestamp.plus(order.getDistance(), ChronoUnit.MINUTES);
            order.setCompletionTime(timestamp);

            if(timestamp.isBefore(positiveCompletion(order))) {
                order.setFeedback(Feedback.Promote);
            } else if(timestamp.isBefore(neutralCompletion(order))) {
                order.setFeedback(Feedback.Neutral);
            } else {
                order.setFeedback(Feedback.Detract);
            }

            Utils.println(String.format("- delivered at\t\t%s, %s", timestamp.format(formatter), order.getFeedback()));
            timestamp = timestamp.plus(order.getDistance(), ChronoUnit.MINUTES);
            Utils.println(String.format("- returned at\t\t%s", timestamp.format(formatter)));

            getOrderedList().add(order);
        }
    }

    public static LocalTime startOfDay() {
        return LocalTime.of(6,0,0);
    }

    public static LocalTime endOfDay() {
        return LocalTime.of(22,0,0);
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

    public LocalTime positiveCompletion(Order order) {
        LocalTime desired = order.getTimestamp().plus(120, ChronoUnit.MINUTES);
        LocalTime limit = endOfDay();
        if(desired.isAfter(limit)) desired = limit;
        return desired;
    }

    public LocalTime neutralCompletion(Order order) {
        LocalTime desired = order.getTimestamp().plus(240, ChronoUnit.MINUTES);
        LocalTime limit = endOfDay();
        if(desired.isAfter(limit)) desired = limit;
        return desired;
    }

    public abstract LocalTime optimalDepartureTime(LocalTime currentTimestamp, Order order);
}
