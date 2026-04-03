#  JMX + RMI POC (Heap Monitor)

##  Overview

This project demonstrates how to use **Java Management Extensions (JMX)** with **RMI (Remote Method Invocation)** to monitor and manage a JVM at runtime.

It includes:

* Custom MBean (`HeapMonitor`)
* Runtime attributes (get/set)
* Operations invocation
* Notification mechanism
* Remote client-server interaction via JMX

---

##  Concepts Covered

* JMX Architecture
* MBeans (Standard MBeans)
* Attributes (getters/setters exposed via JMX)
* Operations (methods callable remotely)
* Notifications (event-driven alerts)
* RMI-based remote communication

---

##  Project Structure

```
src/main/java/com/example/jmx/
 ├── HeapMonitorMBean.java   # MBean interface
 ├── HeapMonitor.java        # Implementation + notifications
 ├── JMXServer.java          # Exposes MBean via RMI
 └── JMXClient.java          # Remote client
```

---

##  Features

###  1. Heap Monitoring

Tracks JVM heap usage using:

```java
ManagementFactory.getMemoryMXBean()
```

---

###  2. Runtime Attributes

* `HeapUsed` → Read-only
* `Threshold` → Configurable at runtime

---

###  3. Operations

```java
checkHeap()
```

* Checks current heap usage
* Triggers alert if threshold exceeded

---

###  4. Notifications

* Sends alert when:

```text
HeapUsed > Threshold
```

Example:

```
ALERT RECEIVED: Heap exceeded threshold: 12582912
```

---

## How It Works

```
Client → JMXConnectorFactory → RMI → JMX Server → MBean → Response
```

* JMX uses **RMI internally**
* Client interacts using `MBeanServerConnection`
* Calls are executed remotely

---

##  How to Run

### 1️ Build Project

```bash
mvn clean package
```

---

### 2️ Start JMX Server

```bash
java -cp target/jmx-poc-1.0-SNAPSHOT.jar com.example.jmx.JMXServer
```

Output:

```
JMX Server started on port 9999...
```

---

### 3️ Run JMX Client

```bash
java -cp target/jmx-poc-1.0-SNAPSHOT.jar com.example.jmx.JMXClient
```

---

### 4️ Expected Output

```
Connected to JMX Server...
Heap Used: 12582912
Threshold updated
checkHeap() invoked
Listening for notifications...

ALERT RECEIVED: Heap exceeded threshold: 12582912
---

##  Limitations

* Uses RMI (not cloud-friendly)
* Requires open ports
* Java-specific


