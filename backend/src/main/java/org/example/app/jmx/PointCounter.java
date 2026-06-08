package org.example.app.jmx;

import org.example.app.dto.PointResponse;
import org.springframework.stereotype.Component;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PointCounter extends NotificationBroadcasterSupport implements PointCounterMXBean {
    private final AtomicLong totalPoints = new AtomicLong(0);
    private final AtomicLong totalMisses = new AtomicLong(0);
    private final Map<String, UserStats> userStatsMap = new ConcurrentHashMap<>();

    private long notificationSequence = 1;

    public void registerPoint(PointResponse response) {
        if (response == null || response.getUsername() == null) {
            System.err.println("Error in registering point without username");
            return;
        }
        boolean isHit = response.getIsHit();
        int quarter = getQuarter(response.getX(), response.getY());

        totalPoints.incrementAndGet();
        if (!isHit) {
            totalMisses.incrementAndGet();
        }
        userStatsMap.compute(response.getUsername(), (username, stats) -> {
            if (stats == null) {
                stats = new UserStats(username);
            }
            stats.registerPoint(isHit, quarter);
            if (stats.getStreakMisses() == 3) {
                sendNotification(username);
                stats.dropStreakMisses();
            }
            return stats;
        });
    }

    private int getQuarter(double x, double y) {
        if (x >= 0 && y >= 0) return 1;
        if (x < 0 && y >= 0) return 2;
        if (x < 0 && y < 0) return 3;
        return 4;
    }

    private void sendNotification(String username) {
        super.sendNotification(new Notification(
                "user.streak.misses",
                this,
                notificationSequence++,
                System.currentTimeMillis(),
                "User '" + username + "' 3 times mozilla :)"
        ));
    }

    @Override
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public long getMissPoints() {
        return totalMisses.get();
    }

    @Override
    public Map<String, UserStats> getUsersStats() {
        return userStatsMap;
    }
}