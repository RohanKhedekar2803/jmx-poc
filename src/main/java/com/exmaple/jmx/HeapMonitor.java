package com.exmaple.jmx;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.lang.management.ManagementFactory;

public class HeapMonitor extends NotificationBroadcasterSupport implements HeapMonitorMBean {

    private long threshold = 50_000_000;
    private long seq = 1;

    @Override
    public long getHeapUsed() {
        return ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage()
                .getUsed();
    }

    @Override
    public long getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(long value) {
        this.threshold = value;
    }

    @Override
    public void checkHeap() {
        long used = getHeapUsed();

        if (used > threshold) {
            Notification n = new Notification(
                    "heap.alert",
                    this,
                    seq++,
                    System.currentTimeMillis(),
                    "Heap exceeded threshold: " + used
            );

            sendNotification(n);
        }
    }
}