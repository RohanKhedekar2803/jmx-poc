package com.exmaple.jmx;

import javax.management.*;
import javax.management.remote.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class JMXServer {

    public static void main(String[] args) throws Exception {

        // =====================================================================
        // STEP 1: Create RMI Registry on a fixed port
        // =====================================================================
        // RMI (Remote Method Invocation) needs a registry — a phone book where
        // remote objects are registered by name so clients can look them up.
        //
        // LocateRegistry.createRegistry(9999) starts an RMI registry INSIDE
        // this JVM listening on port 9999. Without this, the client has no way
        // to discover the JMX connector stub.
        //
        // This is equivalent to running `rmiregistry 9999` externally, but
        // doing it in-process is simpler.
        // =====================================================================
        LocateRegistry.createRegistry(9999);

        // =====================================================================
        // STEP 2: Get the MBeanServer for this JVM
        // =====================================================================
        // MBeanServer is the CORE of JMX — it's the in-JVM registry that hosts
        // all MBeans. Think of it as a local database of manageable objects.
        //
        // getPlatformMBeanServer() returns the JVM's built-in MBeanServer which
        // already has system MBeans registered (MemoryMXBean, ThreadMXBean, etc.)
        //
        // Any MBean registered here can be queried/invoked remotely once we
        // expose this server via a JMXConnectorServer (Step 4).
        // =====================================================================
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // =====================================================================
        // STEP 3: Register MBeans with unique ObjectNames
        // =====================================================================
        // Each MBean needs:
        //   (a) An INSTANCE of the MBean class (the actual object with logic)
        //   (b) A UNIQUE ObjectName — acts as the MBean's address/ID
        //
        // ObjectName format: "domain:key1=value1,key2=value2"
        //   - domain  → namespace (e.g., com.example)
        //   - keys    → identify the MBean (type, name, etc.)
        //
        // The MBeanServer uses the interface name convention:
        //   If class = HeapMonitor, it looks for HeapMonitorMBean interface
        //   and exposes only the methods defined in that interface.
        // =====================================================================

        // Register HeapMonitor MBean
        ObjectName heapName = new ObjectName("com.example:type=HeapMonitor");
        HeapMonitor heapMbean = new HeapMonitor();
        mbs.registerMBean(heapMbean, heapName);

        // Register MemoryMonitor MBean
        ObjectName memoryName = new ObjectName("com.example:type=MemoryMonitor");
        MemoryMonitor memoryMbean = new MemoryMonitor();
        mbs.registerMBean(memoryMbean, memoryName);

        // =====================================================================
        // STEP 4: Expose MBeanServer remotely via JMXConnectorServer
        // =====================================================================
        // JMXServiceURL defines HOW and WHERE remote clients connect:
        //   "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi"
        //
        //   Breakdown:
        //     service:jmx        → JMX protocol
        //     rmi                → transport = RMI
        //     ///jndi/rmi://     → lookup via JNDI in the RMI registry
        //     localhost:9999     → host:port of the RMI registry (Step 1)
        //     /jmxrmi            → the binding name in the registry
        //
        // JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs)
        //   - url → where to listen
        //   - env → environment map (auth, SSL, etc.) — null = defaults
        //   - mbs → the MBeanServer to expose (from Step 2)
        //
        // cs.start() does the following internally:
        //   1. Creates an RMI server object (RMIServerImpl)
        //   2. Exports it so it can receive remote calls
        //   3. Binds a STUB of this server into the RMI registry at port 9999
        //      under the name "jmxrmi"
        //   → Now any client can look up "jmxrmi" in the registry, get the
        //     stub, and use it to call MBean operations remotely.
        // =====================================================================
        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");

        JMXConnectorServer cs =
                JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);

        cs.start();

        System.out.println("JMX Server started on port 9999...");
        System.out.println("Registered MBeans: " + heapName + ", " + memoryName);

        // Keep server alive + periodically trigger heap check
        while (true) {
            heapMbean.checkHeap();   // triggers notification if threshold exceeded
            Thread.sleep(2000);
        }
    }
}