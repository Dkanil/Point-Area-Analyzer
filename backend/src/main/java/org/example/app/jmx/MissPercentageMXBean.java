package org.example.app.jmx;

import java.util.Map;

public interface MissPercentageMXBean {
    Map<String, Double> getMissPercentages();
    double getTotalMissPercentage();
}
