package dev.tujger.ddc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class OrdersControllerPredefinedList extends OrdersControllerLiveList {

    public static Integer IN_ADVANCE_MAX = 8;
    public static Integer IN_ADVANCE_RECALCULATE = 2;

    public static Boolean FEE_LATE = true;
    public static Boolean FEE_COMPARING = false;
    public static Boolean FEE_DISTANCE = true;

    private List<Order> optimalQueue = new ArrayList<>();
    private float optimalNPS;
    private long count;
    private double maxCount;

    @Override
    public Order fetchNextOrder(final Date timestamp) {
        if(optimalQueue.size() <= IN_ADVANCE_RECALCULATE) {
            ArrayList<Order> ordersQueue = new ArrayList<>();
            for (Order order : getOrders()) {
                if (order.getFeedback() == null && ordersQueue.size() < IN_ADVANCE_MAX) {
                    Date optimalDepartureTime = optimalDepartureTime(timestamp, order);
                    Date lastNeutralTime = neutralCompletion(order);
                    if (timestamp.after(optimalDepartureTime) || timestamp.equals(optimalDepartureTime)) {
                        ordersQueue.add(order);
                    } else if (timestamp.before(lastNeutralTime) || timestamp.equals(lastNeutralTime)) {
                        ordersQueue.add(order);
                    }
                }
            }
            List<Integer> initialQueue = new ArrayList<>();
            for (int i = 0; i < ordersQueue.size(); i++) {
                initialQueue.add(i);
            }
            count = 0;
            maxCount = calculateFactorial(initialQueue.size());
            optimalNPS = -1e15F;
            optimalQueue.clear();

            /*
             * Iterate over all permutations of queue, calculate NPS on each step trying to find the highest.
             * This highest will be the optimal queue of selected orders.
             */
            permute(initialQueue, 0, queue -> {
                if (count++ % 100 == 0)
                    Utils.print(String.format("\rPermutation %d/%.0f: %s", count, maxCount, Arrays.toString(queue.toArray())));

                float positive = 0F;
                float negative = 0F;

                Date currentTime = new Date(timestamp.getTime());
                for (int i : queue) {
                    Order order = ordersQueue.get(i);
                    Date newTimestamp = optimalDepartureTime(currentTime, order);
                    if (newTimestamp.after(currentTime)) {
                        currentTime = newTimestamp;
                    }
                    currentTime = Utils.modifyTime(currentTime, order.getDistance() * 60);
                    if (FEE_LATE) {
                        if (currentTime.before(positiveCompletion(order))) {
                            positive++;
                            positive++;
                        } else if (currentTime.before(neutralCompletion(order))) {
                            positive++;
                        } else {
                            negative++;
                            negative++;
                        }
                    }
                    if (FEE_COMPARING) {
                        float fee = (currentTime.getTime() - order.getTimestamp().getTime()) / 1000F;
                        if (fee > 240) fee = fee / 60 / 60 * 2;
                        else if (fee > 120) fee = fee / 60 / 60;
                        else fee = fee / 60 / 60 / 2;
                        negative += fee;
                    }
                    if (FEE_DISTANCE) negative += order.getDistance() / 60F;

                    currentTime = Utils.modifyTime(currentTime, order.getDistance() * 60);
                }
                float promoters = 1F * positive / ordersQueue.size();
                float detractors = 1F * negative / ordersQueue.size();
                float nps = (promoters - detractors) * 100F;
                if (nps > optimalNPS) {
                    optimalNPS = nps;
                    optimalQueue.clear();
                    for (int i : queue) {
                        optimalQueue.add(ordersQueue.get(i));
                    }
                }
            });
            Utils.print("\r");
        }
        while(optimalQueue.size() > 0) {
            Order order = optimalQueue.remove(0);
            /*
             * Check if drone will return to base within work hours.
             */
            if(Utils.modifyTime(timestamp, order.getDistance() * 60).after(endOfDay())) continue;
            return order;
        }
        return null;
    }

    @Override
    public Date optimalDepartureTime(Date currentTimestamp, Order order) {
        Date desired = Utils.modifyTime(order.getTimestamp(), - order.getDistance() * 60);
        Date limit = OrdersController.endOfDay();
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

    private double calculateFactorial(int n){
        double result = 1;
        for (int i = 1; i <=n; i ++){
            result = result*i;
        }
        return result;
    }
}
