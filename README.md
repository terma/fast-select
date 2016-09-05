[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/github/terma/fast-select/badge.svg?branch=master)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

# fast-select 

* [Overview](#overview)
* [Where To Use](#where-to-use)
* [How To Use](#how-to-use)
* [Arhitecture](#arhitecture)
* [Performance](#performance)
* [Memory](#memory)

## Overview

Hi. ```FastSelect``` is extremelly compact in-memory storage with ultra fast access by any filtering criteria but read only access. 

## Where To Use

```FastSelect``` provides very compact storage with extremelly fast filtering by any dimension.

## How To Use

### Create Data Model

You need to create class which will represent you model:
```java
public class People {
    public byte age;
    public String name;
}
```
```fast-select``` supports: ```byte, short, int, long, double, String, byte[], short[], int[], long[]```

### Add data
```java
FastSelect<People> database = new FastSelectBuilder<>(People.class).create();
// add your data, you can call that method multiple times
database.addAll(new ArrayList<People>(...)); 
```
### Select all data
```java
List<People> people = fastSelect.select();
```
### Filter data
For example you want to select people with age between 16 and 32:
```java
List<People> relativeYoungPeople = fastSelect.select(new ByteBetweenRequest("age", 16, 32));
```

### Filter data by multiple criteria
Means ```AND```
```java
List<People> relativeYoungPeople = fastSelect.select(
    new ByteBetweenRequest("age", 16, 32),
    new StringNoCaseLikeRequest("name", "an")
  );
```

### Filter data by at least one criteria
Means ```OR```
```java
List<People> relativeYoungPeople = fastSelect.select(
    new OrRequest(
      new ByteBetweenRequest("age", 16, 32),
      new StringNoCaseLikeRequest("name", "an")
    )  
  );
```

### Filter and sorting
```java
ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
fastSelect.selectAndSort(new Request[] {new ByteBetweenRequest("age", 16, 32)}, callback, "a");
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
