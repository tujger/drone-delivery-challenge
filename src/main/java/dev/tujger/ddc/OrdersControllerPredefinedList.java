package dev.tujger.ddc;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public Order fetchNextOrder(final LocalTime timestamp) {
        if(optimalQueue.size() <= IN_ADVANCE_RECALCULATE) {
            ArrayList<Order> ordersQueue = new ArrayList<>();
            for (Order order : getOrders()) {
                if (order.getFeedback() == null && ordersQueue.size() < IN_ADVANCE_MAX) {
                    LocalTime optimalDepartureTime = optimalDepartureTime(timestamp, order);
                    LocalTime lastNeutralTime = neutralCompletion(order);
                    if (timestamp.isAfter(optimalDepartureTime) || timestamp.equals(optimalDepartureTime)) {
                        ordersQueue.add(order);
                    } else if (timestamp.isBefore(lastNeutralTime) || timestamp.equals(lastNeutralTime)) {
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

                LocalTime currentTime = LocalTime.from(timestamp);
                for (int i : queue) {
                    Order order = ordersQueue.get(i);
                    LocalTime newTimestamp = optimalDepartureTime(currentTime, order);
                    if (newTimestamp.isAfter(currentTime)) {
                        currentTime = newTimestamp;
                    }
                    currentTime = currentTime.plus(order.getDistance(), ChronoUnit.MINUTES);
                    if (FEE_LATE) {
                        if (currentTime.isBefore(positiveCompletion(order))) {
                            positive++;
                            positive++;
                        } else if (currentTime.isBefore(neutralCompletion(order))) {
                            positive++;
                        } else {
                            negative++;
                            negative++;
                        }
                    }
                    if (FEE_COMPARING) {
                        float fee = Duration.between(currentTime, order.getTimestamp()).getSeconds() * 1F;
                        if (fee > 240 * 60) fee = fee / 60 / 60 * 2;
                        else if (fee > 120 * 60) fee = fee / 60 / 60;
                        else fee = fee / 60 / 60 / 2;
                        negative += fee;
                    }
                    if (FEE_DISTANCE) negative += order.getDistance() / 60F;

                    currentTime = currentTime.plus(order.getDistance(), ChronoUnit.MINUTES);
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
            if(timestamp.plus(order.getDistance(), ChronoUnit.MINUTES).isAfter(endOfDay())) continue;
            return order;
        }
        return null;
    }

    @Override
    public LocalTime optimalDepartureTime(LocalTime currentTimestamp, Order order) {
        LocalTime desired = order.getTimestamp().minus(order.getDistance(), ChronoUnit.MINUTES);
        LocalTime limit = OrdersController.endOfDay();
        if(desired.isAfter(limit)) desired = limit;
        if(desired.isBefore(currentTimestamp)) desired = currentTimestamp;
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
