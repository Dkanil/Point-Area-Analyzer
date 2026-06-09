package org.example.app.jmx;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ManagedResource(objectName = "MyMBeans:name=MissPercentage")
public class MissPercentage implements MissPercentageMXBean {
    private final PointCounter pointCounter;

    public MissPercentage(PointCounter pointCounter) {
        this.pointCounter = pointCounter;
    }

    @Override
    public Map<String, Double> getMissPercentages() {
        Map<String, Double> mp = new HashMap<>();
        for (Map.Entry<String, UserStats> entry : pointCounter.getUsersStats().entrySet()) {
            UserStats userStats = entry.getValue();
            long total = userStats.getTotal();
            double percentage = (total == 0) ? 0.0 : (double) userStats.getMisses() / total * 100;
            mp.put(entry.getKey(), percentage);
        }
        return mp;
    }

    @Override
    public double getTotalMissPercentage() {
        long total = pointCounter.getTotalPoints();
        return (total == 0) ? 0.0 : (double) pointCounter.getMissPoints() / total * 100;
    }
}
