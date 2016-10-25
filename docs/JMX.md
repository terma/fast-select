To publish information by JMX about instance of FastSelect you can use embedded class ```FastSelectMXBeanImpl``` from package ```com.github.terma.fastselect.jmx``` It provide read-only info like:
* size (count of records)
* allocated size
* used mem
* columns (type, name, mem)

### To register FastSelect instance by JMX
```java
FastSelect<Object> fastSelect = ...;
FastSelectMXBean fastSelectMXBean = new FastSelectMXBeanImpl(fastSelect);
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
mbs.registerMBean(fastSelectMXBean, new ObjectName("fastselect:type=mbeanname"));
```

### Unregister 
Use standard way for MBeans:
```java
String mbeanName = ...;
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
mbs.unregisterMBean(new ObjectName("fastselect:type=mbeanname"));
```
