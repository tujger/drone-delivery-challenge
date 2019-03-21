package dev.tujger.ddc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class OrdersControllerLiveList extends OrdersController {

    @Override
    public Order fetchNextOrder(Date timestamp) {
        ArrayList<Order> simultaneous = new ArrayList<>();

        /*
         * First of all, try to increase NPS, look for orders on time
         */
        for(Order order: getOrders()) {
            if(order.getFeedback() == null
                       && (timestamp.after(order.getTimestamp()) || timestamp.equals(order.getTimestamp()))
                       && (timestamp.before(positiveCompletion(order)) || timestamp.equals(positiveCompletion(order)))) {
                simultaneous.add(order);
            }
        }

        /*
         * Second, try to deliver neutral orders
         */
        if(simultaneous.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null
                            && (timestamp.after(order.getTimestamp()) || timestamp.equals(order.getTimestamp()))
                            && (timestamp.before(neutralCompletion(order)) || timestamp.equals(neutralCompletion(order)))) {
                    simultaneous.add(order);
                }
            }
        }

        /*
         * Third, try to deliver first not delivered neutral order in queue, without sorting
         */
        if(simultaneous.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null
                            && (timestamp.before(neutralCompletion(order)) || timestamp.equals(neutralCompletion(order)))) {
                    simultaneous.add(order);
                    break;
                }
            }
        }

        /*
         * Finally, deliver expired orders
         */
        if(simultaneous.isEmpty()) {
            for (Order order : getOrders()) {
                if (order.getFeedback() == null) {
                    simultaneous.add(order);
                }
            }
        }

        simultaneous.sort(sortSimultaneous);

        while(simultaneous.size() > 0) {
            Order order = simultaneous.remove(0);
            /*
             * Check if drone will return to base within work hours.
             */
            if(Utils.modifyTime(timestamp, order.getDistance() * 120).after(endOfDay())) continue;
            return order;
        }

        return null;
    }

    private Comparator<Order> sortSimultaneous = (o1, o2) -> {
        if (o1.getDistance() < o2.getDistance()) return -1;
        else if (o1.getDistance() > o2.getDistance()) return 1;
        else if (o1.getTimestamp().before(o2.getTimestamp())) return -1;
        else if (o1.getTimestamp().after(o2.getTimestamp())) return 1;
        else return 0;
    };

    @Override
    public Date optimalDepartureTime(Date currentTimestamp, Order order) {
        Date desired = Utils.modifyTime(order.getTimestamp(), 0);
        Date limit = endOfDay();
        if(desired.after(limit)) desired = limit;
        if(desired.before(currentTimestamp)) desired = currentTimestamp;
        return desired;
    }

}
