package com.exmaple.jmx;

import javax.management.*;
import javax.management.remote.*;

public class JMXClient {

    public static void main(String[] args) throws Exception {

        // 1. Connect to JMX Server
        String urlStr = "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi";
        JMXServiceURL url = new JMXServiceURL(urlStr);

        JMXConnector jmxc = JMXConnectorFactory.connect(url);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        System.out.println("Connected to JMX Server...");

        // 2. MBean name (must match server)
        ObjectName name = new ObjectName("com.example:type=HeapMonitor");

        // 3. Read attribute
        Long heap = (Long) mbsc.getAttribute(name, "HeapUsed");
        System.out.println("Heap Used: " + heap);

        // 4. Set attribute (dynamic change)
        mbsc.setAttribute(name, new Attribute("Threshold", 1L));
        System.out.println("Threshold updated");

        // 5. Call operation
        mbsc.invoke(name, "checkHeap", null, null);
        System.out.println("checkHeap() invoked");

        // 6. Listen for notifications
        mbsc.addNotificationListener(name, (notification, handback) -> {
            System.out.println("ALERT RECEIVED: " + notification.getMessage());
        }, null, null);

        System.out.println("Listening for notifications...");

        // 7. Keep client alive to receive notifications
        Thread.sleep(Long.MAX_VALUE);
    }
}