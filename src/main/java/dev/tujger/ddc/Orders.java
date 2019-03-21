package dev.tujger.ddc;

import java.util.List;

public interface Orders extends List<Order> {

    void update() throws Exception;

    void setSource(String source);

    String getSource();

}
