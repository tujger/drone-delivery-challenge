package dev.tujger.ddc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class DeliveryControllerLiveList extends DeliveryController {

    @Override
    public Order fetchNextOrder(Date timestamp) {
        ArrayList<Order> simultaneous = new ArrayList<>();
        for(Order order: getOrders()) {
            if(order.getFeedback() == null
                       && (timestamp.after(leftPositive(order)) || timestamp.equals(leftPositive(order)))
                       && (timestamp.before(rightPositive(order)) || timestamp.equals(rightPositive(order)))) {
                simultaneous.add(order);
            }
        }
        if(simultaneous.size() > 0) {
            simultaneous.sort(sortSimultaneous);
            return simultaneous.get(0);
        }

        for(Order order: getOrders()) {
            if(order.getFeedback() == null
                       && (timestamp.after(leftNeutral(order)) || timestamp.equals(leftNeutral(order)))
                       && (timestamp.before(rightNeutral(order)) || timestamp.equals(rightNeutral(order)))) {
                simultaneous.add(order);
            }
        }
        if(simultaneous.size() > 0) {
            simultaneous.sort(sortSimultaneous);
            return simultaneous.get(0);
        }

        for(Order order: getOrders()) {
            if(order.getFeedback() == null && timestamp.before(rightNeutral(order))) {
                return order;
            }
        }
        for(Order order: getOrders()) {
            if(order.getFeedback() == null) {
                return order;
            }
        }
        return null;
    }

    Comparator<Order> sortSimultaneous = (o1, o2) -> {
        if (o1.getDistance() < o2.getDistance()) return -1;
        else if (o1.getDistance() > o2.getDistance()) return 1;
        else if (o1.getTimestamp().before(o2.getTimestamp())) return -1;
        else if (o1.getTimestamp().after(o2.getTimestamp())) return 1;
        else return 0;
    };

    @Override
    public Date leftPositive(Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 0);
        Date limit = DeliveryController.startOfDay();
        if(desired.before(limit)) desired = limit;
        return desired;
    }

    @Override
    public Date leftNeutral(Order order) {
        return leftPositive(order);
    }

    @Override
    public Date rightPositive(Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 120 * 60);
        Date limit = DeliveryController.endOfDay();
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    @Override
    public Date rightNeutral(Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 240 * 60);
        Date limit = DeliveryController.endOfDay();
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    @Override
    public Date optimalDepartureTime(Date currentTimestamp, Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 0);
        Date limit = DeliveryController.endOfDay();
        if(desired.after(limit)) desired = limit;
        if(desired.before(currentTimestamp)) desired = currentTimestamp;
        return desired;
    }

}
