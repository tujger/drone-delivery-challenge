package dev.tujger.ddc;

import java.util.Date;
import java.util.List;

public interface Orders extends List<Order> {
    void update() throws Exception;

    Order nextAvailable(Date timestamp);

    void setSource(String source);

    String getSource();
}
