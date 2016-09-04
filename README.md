[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/github/terma/fast-select/badge.svg?branch=master)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

# fast-select 

* [Overview](#overview)
* [Use Cases](#use-cases)
* [How To Use](#how-to-use)
* [Arhitecture](#arhitecture)
* [Performance](#performance)
* [Memory](#memory)

## Overview

TBD

## Use Cases

### Where FastSelect will not help

Lookup data by one key. ```HashMap``` will be faster.

I need to query online data which updates each 1 sec! ```FastSelect``` can't help here as to query it you need to create it. Means spend some time and see old data.

I want to query data but in same time want to update it frequently. Data in ```FastSelect``` is snapshot you can't update it. So take in account time on cache creation. If you able to create cache in 1 min but users agree only with 30 sec delay. Cache is not your option. Opposite case: In one of production use case for ```FastSelect``` user happy to see online data with 5 min delay. While cache creation time is 30 sec.

### Where FastSelect helps

TBD

## How To Use

Create Data Class. That class will represent you model. ```fast-select``` supports: ```byte, short, int, long, double, String, byte[], short[], int[], long[]```

```java
public class People {
    public byte age;
    public String name;
}
```
When you have model. Let's try to populate storage with data:
```java
FastSelect<People> database = new FastSelectBuilder<>(People.class).create();
// add your data
database.addAll(new ArrayList<People>(...)); 
```
You have storage which we can start to query. For example we want to find people from 16 to 32:
```java
List<People> relativeYoungPeople = fastSelect.select(new ByteBetweenRequest("age", 16, 32));
```
That's too simple and silly. If you have list of 10000 people and you want to provide user simple search by age. Probably you don't need to use ```fast-select```. Real case when you have 16M of People and 100 concurrent users want to search them by any possible attribute for People and result should.
```java
MultiGroupCountCallback callback = new MultiGroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(where, callback);
callback.getCounters(); // your result here grouped by field 'a'
```

### Select with sorting
```java
ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
fastSelect.selectAndSort(where, callback, "a");
callback.getResult();
```

More use cases you can find in javadoc ```callbacks``` package

## Arhitecture

TBD

## Performance

In-memory storage with low latency access for online analytic with huge volume of concurrent users and rare updates.

* In-memory (embedded) column orineted storage aka [HOLAP](https://en.wikipedia.org/wiki/HOLAP)
* Almost read only (you able to add data. Updating and delete subject of future releases)
* Zero Java Object overhead (only data what you want to store)
* Low latency ```~15 msec per 1m```
* Non blocking, lock free
* API to support fast filtering, grouping, sorting, selecting
* Small lib ```~53 kb```
* Free open source under Apache 2.0 license.
* [Javadoc](http://terma.github.io/fast-select/)

## Memory

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
