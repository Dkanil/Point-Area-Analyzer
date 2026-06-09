package org.example.app.jmx;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Threshold;
import jdk.jfr.Event;

@Label("Point Set Event")
@Description("Records when user set point")
@Category("Point Events")
@Threshold("1 ms")
public class PointSetEvent extends Event {
    @Label("Username")
    private final String username;

    @Label("X Coordinate")
    private final double x;

    @Label("Y Coordinate")
    private final double y;

    @Label("Is Hit")
    public boolean isHit;

    public PointSetEvent(String username, double x, double y) {
        this.username = username;
        this.x = x;
        this.y = y;
    }
}