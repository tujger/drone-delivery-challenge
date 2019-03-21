package dev.tujger.ddc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class DeliveryControllerPredefinedList extends DeliveryControllerLiveList {

    private static final Integer IN_ADVANCE = 8;

    private List<Order> optimalQueue = new ArrayList<>();
    private float optimalNPS;
    private long count;
    private long maxCount;

    @Override
    public Order fetchNextOrder(final Date timestamp) {
        ArrayList<Order> ordersQueue = new ArrayList<>();
        for(Order order: getOrders()) {
            if(order.getFeedback() == null && ordersQueue.size() < IN_ADVANCE
                       && (timestamp.after(optimalDepartureTime(timestamp, order)) || timestamp.equals(optimalDepartureTime(timestamp, order)))) {
                ordersQueue.add(order);
            }
        }
        List<Integer> initialQueue = new ArrayList<>();
        for(int i = 0; i < ordersQueue.size(); i++) {
            initialQueue.add(i);
        }
        count = 0;
        maxCount = calculateFactorial(initialQueue.size());
        optimalNPS = -1e15F;
        optimalQueue.clear();
        permute(initialQueue, 0, queue -> {
            count++;
            if(count % 1000 == 0) System.out.print("\r(" + count + "/" + maxCount + ") " + Arrays.toString(queue.toArray()));

            float positive = 0F;
            float negative = 0F;

            Date currentTime = new Date(timestamp.getTime());
            for(int i: queue) {
                Order order = ordersQueue.get(i);
                Date newTimestamp = optimalDepartureTime(currentTime, order);
                if(newTimestamp.after(currentTime)) {
                    currentTime = newTimestamp;
                }

                currentTime = DeliveryController.modifyTime(currentTime, order.getDistance() * 60);
                if(currentTime.before(rightPositive(order)) && currentTime.after(leftPositive(order))) {
                    positive++;
                    positive++;
                } else if(currentTime.before(rightNeutral(order)) && currentTime.after(leftNeutral(order))) {
                    positive++;
                } else {
                    negative++;
                    negative++;
                }
                negative += (currentTime.getTime() - order.getTimestamp().getTime())/1000F/60/60;
                negative += order.getDistance()/60F;

                currentTime = DeliveryController.modifyTime(currentTime, order.getDistance() * 60);
            }
            float promoters = 1F * positive / ordersQueue.size();
            float detractors = 1F * negative / ordersQueue.size();
            float nps = (promoters - detractors) * 100F;
            if(nps > optimalNPS) {
                optimalNPS = nps;
                optimalQueue.clear();
                for(int i: queue) {
                    optimalQueue.add(ordersQueue.get(i));
                }
            }
            if(count % 1000 == 0) System.out.print("\r");
        });
        if(optimalQueue.size() > 0) return optimalQueue.get(0);
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
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), -order.getDistance() * 60);
        Date limit = DeliveryController.startOfDay();
        if(desired.before(limit)) desired = limit;
        return desired;
    }

    @Override
    public Date optimalDepartureTime(Date currentTimestamp, Order order) {
        Date desired = DeliveryController.modifyTime(order.getTimestamp(), -order.getDistance() * 60);
        Date limit = DeliveryController.endOfDay();
        if(desired.after(limit)) desired = limit;
        if(desired.before(currentTimestamp)) desired = currentTimestamp;
        return desired;
    }

    private void permute(List<Integer> queue, int k, Consumer<List<Integer>> estimateNPS){
        for(int i = k; i < queue.size(); i++){
            Collections.swap(queue, i, k);
            permute(queue, k+1, estimateNPS);
            Collections.swap(queue, k, i);
        }
        if (k == queue.size() -1){
            estimateNPS.accept(queue);
        }
    }

    private int calculateFactorial(int n){
        int result = 1;
        for (int i = 1; i <=n; i ++){
            result = result*i;
        }
        return result;
    }

 }
