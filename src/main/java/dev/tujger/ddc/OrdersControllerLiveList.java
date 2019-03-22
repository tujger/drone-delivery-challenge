package dev.tujger.ddc;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;

public class OrdersControllerLiveList extends OrdersController {

    @Override
    public Order fetchNextOrder(LocalTime timestamp) {
        ArrayList<Order> actualQueue = new ArrayList<>();

        /*
         * First of all, try to increase NPS, look for orders on time
         */
        for(Order order: getOrders()) {
            if(order.getFeedback() == null
                       && (timestamp.isAfter(order.getTimestamp()) || timestamp.equals(order.getTimestamp()))
                       && timestamp.isBefore(positiveCompletion(order))) {
                actualQueue.add(order);
            }
        }
        /*
         * Second, try to deliver neutral orders
         */
        if(actualQueue.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null
                            && (timestamp.isAfter(order.getTimestamp()) || timestamp.equals(order.getTimestamp()))
                            && timestamp.isBefore(neutralCompletion(order))) {
                    actualQueue.add(order);
                }
            }
        }

        /*
         * Third, try to deliver first not delivered neutral order in queue, without sorting
         */
        if(actualQueue.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null
                            && (timestamp.isBefore(neutralCompletion(order)) || timestamp.equals(neutralCompletion(order)))) {
                    actualQueue.add(order);
                    break;
                }
            }
        }

        /*
         * Finally, deliver expired orders
         */
        if(actualQueue.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null) {
                    actualQueue.add(order);
                }
            }
        }

        actualQueue.sort(sortSimultaneous);

        while(actualQueue.size() > 0) {
            Order order = actualQueue.remove(0);
            /*
             * Check if drone will return to base within work hours.
             */
            if(timestamp.plus(order.getDistance() * 2, ChronoUnit.MINUTES).isAfter(endOfDay())) continue;
            return order;
        }

        return null;
    }

    private Comparator<Order> sortSimultaneous = (o1, o2) -> {
        if (o1.getDistance() < o2.getDistance()) return -1;
        else if (o1.getDistance() > o2.getDistance()) return 1;
        else if (o1.getTimestamp().isBefore(o2.getTimestamp())) return -1;
        else if (o1.getTimestamp().isAfter(o2.getTimestamp())) return 1;
        else return 0;
    };

    @Override
    public LocalTime optimalDepartureTime(LocalTime currentTimestamp, Order order) {
        LocalTime desired = order.getTimestamp();
        LocalTime limit = endOfDay();
        if(desired.isAfter(limit)) desired = limit;
        if(desired.isBefore(currentTimestamp)) desired = currentTimestamp;
        return desired;
    }

}
