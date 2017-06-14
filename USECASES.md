## Speed Up Analytic

#### Problem

* Small or mid size DWH or just ETL (processing) application and you want to
add analytic part without big investment in expensive technologies or hardware
* Just want to try provide analytic and not sure how useful it will
be

#### Solution

* No changes in existent data model
* You just organize data in classic star model during load to fast-select
* Because it's in memory and java object overhead free you can store huge amount 
of data to ```fast-select``` and get all benefits of in memory column oriented 
processing

## Separate Processing and Analytic

#### Problem

* Your have high loaded ETL (processing) application which should provide analytic
on processed data at the same time
* Your ETL framework is good for processing but not for analytic

#### Solution

* fast-select supports append only model

## Speed Up History Analytic

#### Problem

You need to shield data source by cache, but data source is extremely slow. It doesn't able to provide data for on demand cache in acceptable time.

#### Solution

Persistence cache. fast-select supports persistence mode in which you can store cache to dump file and restore cache from that dump. That's very useful when time to create cache from data source is huge.

#### Result

In my production case we have 16m items in DB with 50 fields. Direct call to DB takes ~40 sec. Creating cache from data source takes ~15 min, which is unacceptable. Restoring cache from dump takes ~7 sec. After restoring any request done in ~300 msec

#### How?

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

#### Limitations

Data source should be almost read only so your dump could be created and used.
