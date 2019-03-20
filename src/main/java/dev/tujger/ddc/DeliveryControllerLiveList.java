package dev.tujger.ddc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryControllerLiveList extends DeliveryController {

    @Override
    public List<Order> fetchAvailableOrders(Date timestamp) {
        List<Order> availableOrders = new ArrayList<>();
        for(Order order: getOrders()) {
            if(order.getFeedback() == null && timestamp.before(rightNeutral(order))) {
                availableOrders.add(order);
            }
        }
        return availableOrders;
    }

    @Override
    public Date leftPositive(Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 0);
        Date limit = DeliveryController.startOfDay();
        if(desired.before(limit)) desired = limit;
        return desired;
    }

    @Override
    public Date leftNeutral(Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 0);
        Date limit = DeliveryController.startOfDay();
        if(desired.before(limit)) desired = limit;
        return desired;
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
    public Date startDeliveryTimestamp(Date currentTimestamp, Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), 0);
        Date limit = DeliveryController.endOfDay();
        if(desired.after(limit)) desired = limit;
        if(desired.before(currentTimestamp)) desired = currentTimestamp;
        return desired;
    }

}
