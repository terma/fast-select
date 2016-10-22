## Problem

You need to shield data source by cache, but data source is extremely slow. It doesn't able to provide data for on demand cache in acceptable time.

## Solution

Persistence cache. fast-select supports persistence mode in which you can store cache to dump file and restore cache from that dump. That's very useful when time to create cache from data source is huge.

## Result

In my production case we have 16m items in DB with 50 fields. Direct call to DB takes ~40 sec. Creating cache from data source takes ~15 min, which is unacceptable. Restoring cache from dump takes ~7 sec. After restoring any request done in ~300 msec

## How?

To create dump:
```java
FastSelect<MyCacheItem> fastSelect = ...; 
FileChannel fc = new RandomAccessFile(new File("dump-file"), "rw").getChannel();
fastSelect.save(fc);
```

To load dump:
```java
FastSelect<MyCacheItem> fastSelect = new FastSelectBuilder(MyCacheItem.class).inc(myCacheSize).create();
FileChannel fc = new RandomAccessFile(new File("dump-file"), "rw").getChannel();
fastSelect.load(fc, myCountOfParallelLoadThreads);
# after that you can use cache
# fastSelect.select(...);
```

## Limitations

Data source should be almost read only so your dump could be created and used.
