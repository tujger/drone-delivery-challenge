package dev.tujger.ddc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings({"WeakerAccess", "deprecation"})
class Order {
    private String id;
    private String coordinate;
    private Date timestamp;
    private Date departureTime;
    private Date completionTime;
    private int distance;
    private Feedback feedback;

    private Date currentDate = new Date();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public Order(String inputLine) throws ParseException {
        String[] tokens = inputLine.split("\\s+");
        setId(tokens[0]);
        setCoordinate(tokens[1]);
        setDistance(parseDistance(getCoordinate()));
        setTimestamp(parseDate(tokens[2]));
    }

    private Date parseDate(String token) throws ParseException {
        Date timestamp = dateFormat.parse(token);
        // reinitialize timestamp to dismiss milliseconds effect
        timestamp = new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate(), timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds());
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

    @Override
    public String toString() {
        return String.format("%s, coordinate %s,\tdistance %3d\t\t%s",
                getId(), getCoordinate(), getDistance(),  Utils.formatTime(getTimestamp()))
                       + (getDepartureTime() == null ? "" : String.format(", departed at %s", Utils.formatTime(getDepartureTime())))
                       + (getFeedback() == null ? "" : String.format(", completed at %s, %s", Utils.formatTime(getCompletionTime()), getFeedback()));
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

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }
}
