package dev.tujger.ddc;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@SuppressWarnings({"WeakerAccess"})
class Order {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String id;
    private String coordinate;
    private LocalTime timestamp;
    private LocalTime departureTime;
    private LocalTime completionTime;
    private int distance;
    private Feedback feedback;

    public Order(String inputLine) throws DateTimeParseException {
        String[] tokens = inputLine.split("\\s+");
        setId(tokens[0]);
        setCoordinate(tokens[1]);
        setDistance(parseDistance(getCoordinate()));
        setTimestamp(LocalTime.parse(tokens[2]));
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
                getId(), getCoordinate(), getDistance(), getTimestamp().format(formatter))
                       + (getDepartureTime() == null ? "" : String.format(", departed at %s", getDepartureTime().format(formatter)))
                       + (getFeedback() == null ? "" : String.format(", completed at %s, %s", getCompletionTime().format(formatter), getFeedback()));
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

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
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

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalTime completionTime) {
        this.completionTime = completionTime;
    }
}
