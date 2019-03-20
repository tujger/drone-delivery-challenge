package dev.tujger.ddc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class Order {
    private String id;
    private String coordinate;
    private Date timestamp;
    private int distance;
    private Feedback feedback;

    private Date currentDate = new Date();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Order(String inputLine) throws ParseException {
        String[] tokens = inputLine.split(" ");

        setId(tokens[0]);
        setCoordinate(tokens[1]);
        setTimestamp(parseDate(tokens[2]));
        setDistance(parseDistance(getCoordinate()));
    }

    private Date parseDate(String token) throws ParseException {
        Date timestamp = dateFormat.parse(token);
        timestamp.setYear(currentDate.getYear());
        timestamp.setMonth(currentDate.getMonth());
        timestamp.setDate(currentDate.getDate());
        return timestamp;
    }

    private int parseDistance(String coordinate) {
        String[] tokens = coordinate.split("\\D+");
        int distance = 0;
        for(String token: tokens) {
            try {
                distance += Integer.valueOf(token);
            } catch (Exception ignored) {}
        }
        return distance;
    }

    private Date limitWith(int hour) {
        Date limit = new Date(getTimestamp().getTime());
        limit.setHours(hour);
        limit.setMinutes(0);
        limit.setSeconds(0);
        return limit;
    }

    public Date leftPositive() {
        Date desired = TimeController.add(getTimestamp(), 0);
        Date limit = limitWith(6);
        if(desired.before(limit)) desired = limit;
        return desired;
    }

    public Date leftNeutral() {
        Date desired = TimeController.add(getTimestamp(), 0);
        Date limit = limitWith(6);
        if(desired.before(limit)) desired = limit;
        return desired;
    }

    public Date rightPositive() {
        Date desired = TimeController.add(getTimestamp(), 120);
        Date limit = limitWith(22);
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    public Date rightNeutral() {
        Date desired = TimeController.add(getTimestamp(), 240);
        Date limit = limitWith(22);
        if(desired.after(limit)) desired = limit;
        return desired;
    }

    public Feedback delivered(Date timestamp) {
        if(timestamp.before(rightPositive()) && timestamp.after(leftPositive())) {
            feedback = Feedback.Positive;
        } else if(timestamp.before(rightNeutral()) && timestamp.after(leftNeutral())) {
            feedback = Feedback.Neutral;
        } else {
            feedback = Feedback.Negative;
        }
        return feedback;
    }

    @Override
    public String toString() {
        return String.format("Order %s, required time %d m\t\t%s <<%s <%s> %s>> %s", getId(), getDistance() * 2, TimeController.formatTimestamp(leftNeutral()), TimeController.formatTimestamp(leftPositive()), TimeController.formatTimestamp(getTimestamp()), TimeController.formatTimestamp(rightPositive()), TimeController.formatTimestamp(rightNeutral()))
                + (feedback == null ? "" : ", delivered with " + feedback);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
