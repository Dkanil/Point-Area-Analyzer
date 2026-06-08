package org.example.app.jmx;

public class UserStats {
    private final String username;
    private long total = 0;
    private long hits = 0;
    private long misses = 0;
    private long streakMisses = 0;

    private long q1Hits = 0;
    private long q1Misses = 0;

    private long q2Hits = 0;
    private long q2Misses = 0;

    private long q3Hits = 0;
    private long q3Misses = 0;

    private long q4Hits = 0;
    private long q4Misses = 0;

    public UserStats(String username) {
        this.username = username;
    }

    public void registerPoint(boolean isHit, int quarter) {
        this.total++;
        if (isHit) {
            this.hits++;
            this.streakMisses = 0;
        } else {
            this.misses++;
            this.streakMisses++;
        }
        switch (quarter) {
            case 1 -> { if (isHit) q1Hits++; else q1Misses++; }
            case 2 -> { if (isHit) q2Hits++; else q2Misses++; }
            case 3 -> { if (isHit) q3Hits++; else q3Misses++; }
            case 4 -> { if (isHit) q4Hits++; else q4Misses++; }
        }
    }
    public void dropStreakMisses() {
        this.streakMisses = 0;
    }

    public String getUsername() {
        return username;
    }

    public long getTotal() {
        return total;
    }

    public long getHits() {
        return hits;
    }

    public long getMisses() {
        return misses;
    }

    public long getStreakMisses() {
        return streakMisses;
    }

    public long getQ1Hits() {
        return q1Hits;
    }

    public long getQ1Misses() {
        return q1Misses;
    }

    public long getQ2Hits() {
        return q2Hits;
    }

    public long getQ2Misses() {
        return q2Misses;
    }

    public long getQ3Hits() {
        return q3Hits;
    }

    public long getQ3Misses() {
        return q3Misses;
    }

    public long getQ4Hits() {
        return q4Hits;
    }

    public long getQ4Misses() {
        return q4Misses;
    }
}
