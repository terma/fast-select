# fast-select

[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/github/terma/fast-select/badge.svg?branch=master)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

Compact in-memory read-only storage with lock free ultra-fast quering by any attributes under Apache 2.0 license.

* [Key Properties](#key-properties)
* [Use Cases](#use-cases)
* [Architecture](docs/ARHI.md), [Performance](docs/PERF.md), [Javadoc](http://terma.github.io/fast-select/)
* [How To Use](#how-to-use)
  * [Aggregate/Group By/Pivot](#aggregate)
  * [Filter, Sort and first 25](#select-first-25-items-from-sorted-dataset)
  * [Filter, Sort and get page](#filter-dataset-get-total-and-render-only-one-page)
  * [JMX](#jmx)
  * [Low Cardinality Strings](#low-cardinality-strings)

## Key Properties

* Compact 
  * No java object overhead (in avg ```x10``` less than object representation)
  * Compact string representation (```UTF-8``` instead of Java ```UTF-16```)
  * String data compression
  * Small metadata footprint (no indexes overhead)
* Fast
  * All dimension available for search
  * Using data statistic to avoid full scan
  * Column oriented
  * Thread safe and lock free
* Support fast save/load to/from disk [details](USECASES.md)
* Small jar file
* Apache 2.0

## Use Cases

* Speed up analytical quering by caching main data in compact and query optimized way instead of using expensive solution [details](USECASES.md#speed-up-analytic)
* Separate ETL and analytic load by keeping main data optimized for processing and add compact model optimizing for quering [details](USECASES.md#separate-processing-and-analytic)
* Sub second quering of historical data by loading portion of data on demand in a seconds [details](USECASES.md#speed-up-history-analytic)

## How to use

### Create Data Class

```java
public class Data {
    public byte a;
    public byte b;
}
```

### Build storage

```java
FastSelect<Data> database = new FastSelectBuilder<>(Data.class).create();

// add your data
database.addAll(new ArrayList<Data>(...)); 
```

### Aggregate

In case if you just want ```select F1, F2, count(X) ... group by F1, F2```
```java
MultiGroupCountCallback callback = new MultiGroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(
  new Request[] {new IntRequest("a", new int[]{12, 3})}, 
  callback);
callback.getCounters(); // your result here grouped by field 'a'
```

Starting from version ```3.2.0``` For more sophisticated and flexible cases you can use ```AggregateCallback``` which support user defined aggregation in fast way, so you don't need to worry about aggregation key perfromance etc.
```java
FastSelect fastSelect = ...;
final ByteData data = fastSelect.getData("columnWithData");
AggregateCallback<MutableInt> callback = new AggregateCallback<>(
    new Aggregator<MutableInt>() {
        @Override
        public MutableInt create(int position) {
           // will be called when this unique key happens first time
           return new MutableInt(data.data[position]);
        }
        
        @Override
        public void aggregate(MutableInt agg, int position) {
            // will be called all other times
            agg.add(data.data[position]);
        }
    },
    fastSelect.getColumnsByNames().get("aggregationColumn1"),
    fastSelect.getColumnsByNames().get("aggregationColumn2")
    // you can specify any amount of columns with any type
);
fastSelect.select(new Request[0], callback);
Map<AggregateKey, MutableInt> result = callback.getResult();
```

### Select first 25 items from sorted dataset
```java
ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
fastSelect.selectAndSort(where, callback, "a");
callback.getResult();
```

### Filter dataset get total and render only one page
```java
// get ref to real data
IntData id = (IntData) fastSelect.getColumnsByNames().get("id").data;

List<Integer> positions = fastSelect.selectPositions(new Request[] {...});
Collections.sort(positions, new Comparator<Integer>() {
    public int compare(Integer p1, Integer p2) { 
        return id.data[p1] - id.data[p2];
    }
});

// page render
List<Map<String, String>> page = new ArrayList<>();
for (int i = 10; i < 20; i++) {
    int p = positions.get(i);
    Map<String, String> row = new HashMap<>();
    row.put("id", id.data[p]);
    page.add(row);
}

int total = positions.size();
```

### Combine filters by AND

Just add more requests

```java
fastSelect.select(new Request[] {
    new IntRequest("id", 12),
    new StringLikeRequest("name", "bim"); // name like '%bim%'
    ...
});
```

### Combine filters by OR

Wrap requests which should be by OR to ```OrRequest```

```java
new OrRequest(
    new IntRequest("id", 12),
    new StringLikeRequest("name", "bim"); // name like '%bim%'
)
```

### JMX

To publish information by JMX about instance of FastSelect you can use embedded class ```FastSelectMXBeanImpl``` from package ```com.github.terma.fastselect.jmx``` It provide read-only info like:
* size (count of records)
* allocated size
* used mem
* columns (type, name, mem)

#### To register FastSelect instance by JMX
```java
FastSelect<Object> fastSelect = ...;
FastSelectMXBean fastSelectMXBean = new FastSelectMXBeanImpl(fastSelect);
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
mbs.registerMBean(fastSelectMXBean, new ObjectName("fastselect:type=mbeanname"));
```

#### Unregister 
Use standard way for MBeans:
```java
String mbeanName = ...;
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
mbs.unregisterMBean(new ObjectName("fastselect:type=mbeanname"));
```


More use cases you can find in javadoc ```callbacks``` package

### Low Cardinality Strings

```fast-select``` provides very compact storage for ```small``` Java types and String, which is provide you
 mem benefits because of no Java object overhead and ```UTF-8``` compression. However you can
 get even better result for low cardinality columns. Take a look on:
 
 * ```com.github.terma.fastselect.data.StringCompressedByteData```
 * ```com.github.terma.fastselect.data.StringCompressedShortData```
 * ```com.github.terma.fastselect.data.StringCompressedIntData```