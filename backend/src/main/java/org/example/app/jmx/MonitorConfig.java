package org.example.app.jmx;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.monitor.GaugeMonitor;
import java.lang.management.ManagementFactory;

@Component
public class MonitorConfig {

    @PostConstruct
    public void setupMonitor() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            GaugeMonitor monitor = new GaugeMonitor();
            monitor.addObservedObject(new ObjectName("MyMBeans:name=pointCounter"));
            monitor.setObservedAttribute("CurrentStreak");
            monitor.setThresholds(3L, 0L);
            monitor.setNotifyHigh(true);
            monitor.setNotifyLow(false);
            monitor.setGranularityPeriod(1000);
            mbs.registerMBean(monitor, new ObjectName("MyMBeans:name=streakMonitor"));
            monitor.start();
        } catch (Exception e) {
            System.err.println("Error while initializing JMX Monitor: " + e.getMessage());
        }
    }
}