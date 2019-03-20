package dev.tujger.ddc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class Order {
    private String id;
    private String coordinate;
    private Date timestamp;
    private Date startedTime;
    private Date deliveredTime;
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

    public void started(Date timestamp) {
        setStartedTime(timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s, required time %d m\t\t%s", getId(), getDistance() * 2,  DeliveryController.formatTimestamp(getTimestamp()))
                       + (getStartedTime() == null ? "" : String.format(", started at %s",
                DeliveryController.formatTimestamp(getStartedTime())))
                       + (getFeedback() == null ? "" : String.format(", delivered at %s with %s",
                DeliveryController.formatTimestamp(getDeliveredTime()), getFeedback()));
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

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Date getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Date startedTime) {
        this.startedTime = startedTime;
    }

    public Date getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Date deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
}
