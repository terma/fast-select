[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/terma/fast-select/badge.svg?branch=master&service=github)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

## Fast-Select 
In-memory column oriented compact storage with extremely fast search by multiple criterias. Free open source under Apache 2.0 license.

## Test on your hardware

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
FastSelect<Data> database = new FastSelect<>(Data.class, Arrays.asList(
    new FastSelect.Column("a", byte.class),
    new FastSelect.Column("b", byte.class),
));

// add your data
database.addAll(new ArrayList<Data>(...)); 
```
### Create filter criteria 
```java
MultiRequest[] where = new MultiRequest[] {new MultiRequest("a", new int[]{12, 3})};
```

### Get count with grouping
```java
GroupCountCallback callback = new GroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(where, callback);
callback.getCounters(); // your result here grouped by field 'a'
```

More use cases you can find in javadoc ```callbacks``` package
