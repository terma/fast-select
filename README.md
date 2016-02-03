[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/terma/fast-select/badge.svg?branch=master&service=github)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

## Fast-Select 

In-memory storage with low latency access for online analytic with huge volume of concurrent users and rare updates.

* In-memory (embedded) column orineted storage aka [HOLAP](https://en.wikipedia.org/wiki/HOLAP)
* Almost read only (you able to add data. Updating and delete subject of future releases)
* Zero Java Object overhead (only data what you want to store)
* Low latency ```~15 msec per 1m```
* Non blocking, lock free
* API to support fast filtering, grouping, sorting, selecting
* Small lib ```~53 kb```
* Free open source under Apache 2.0 license.

## Memory Usage

Dataset 7 columns: 5 bytes and 2 shorts

| Dataset       | Heap | 
| -------------:|---:|
| 1m records| 5Mb |
| 10m records | 76Mb |
| 300m records | 2.2Gb | 

## How fast and compare with H2

[24 columns](https://github.com/terma/fast-select/blob/master/src/main/java/com/github/terma/fastselect/demo/DemoData.java)

| Benchmark                              | (blockSize)   | (engine) |(volume) |Mode |Cnt    |Score  |
| --------------------------------------:|--------------:|---------:|--------:|----:|------:|------:|
|first 25 where (11 + 6 con + sorting) |     |        H2|   1000000|  avgt     |  1422.091|          ms/op|
|first 25 where (11 + 6 con + sorting) |    1000  |FastSelect |  1000000 | avgt     |    35.328  |        ms/op|
|group 2 columns and where (10k + 6)         |          |        H2 |  1000000 | avgt    |   1767.386   |       ms/op|
|group 2 columns and where (10k + 6)         |        1000  |FastSelect |  1000000 | avgt   |      22.685    |      ms/op|
|group 2 columns and where (11 + 6 con)             |          |        H2 |  1000000 | avgt  |      281.696     |     ms/op|
|group 2 columns and where (11 + 6 con)            |        1000  |FastSelect |  1000000  |avgt |        11.773      |    ms/op|

## Try on your hardware

- Download jar file http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22fast-select%22
- ```bash
java -jar fast-select.jar 1000000 15 # first parameter is volume to test, second duration in sec```

## How to use

### Create Data Class

```java
public class Data {
    public byte a;
    public byte b;
}
```

### Init fast select

```java
FastSelect<Data> database = new FastSelect<>(Data.class);

// add your data
database.addAll(new ArrayList<Data>(...)); 
```
### Create filter criteria 
```java
AbstractRequest[] where = new AbstractRequest[] {new IntRequest("a", new int[]{12, 3})};
```

### Get count with grouping
```java
GroupCountCallback callback = new GroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(where, callback);
callback.getCounters(); // your result here grouped by field 'a'
```

More use cases you can find in javadoc ```callbacks``` package

## In next version probably =)

* Indexes by String field
* filter by < or >
* Sorting
* Serialization/Deserialization
* On fly modification
