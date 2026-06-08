package org.example.app.jmx;

import java.util.Map;

public interface PointCounterMXBean {
    long getTotalPoints();
    long getMissPoints();
    Map<String, UserStats> getUsersStats();
}
