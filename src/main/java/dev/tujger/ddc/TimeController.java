package dev.tujger.ddc;

import java.util.Date;
import java.util.List;

public class TimeController {

    public void estimateRequiredTimes(List<Order> orders) {
        int totalRequiredTime = 0;
        for(Order order:orders) {
            int requiredTime = order.getDistance() * 2;
            System.out.println(order);

            totalRequiredTime += requiredTime;
        }

        System.out.println(String.format("Total time: %.0f:%d", Math.floor(totalRequiredTime / 60.), totalRequiredTime % 60));
    }

    public static String formatTimestamp(Date date) {
        return String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
    }

    public static Date add(Date date, int minutesToAdd) {
        long time = date.getTime();
        time += minutesToAdd * 60 * 1000;
        return new Date(time);
    }

}
