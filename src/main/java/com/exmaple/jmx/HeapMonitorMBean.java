package com.exmaple.jmx;

public interface HeapMonitorMBean {
    long getHeapUsed();
    long getThreshold();
    void setThreshold(long value);

    void checkHeap();
}