package org.example.app.jmx;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Point Miss Event")
@Description("Records when user's point is missed")
@Category("Point Events")
public class PointMissEvent extends Event {
    @Label("Username")
    private final String username;

    @Label("Streak")
    private final long streak;

    public PointMissEvent(String username, long streak) {
        this.username = username;
        this.streak = streak;
    }
}
