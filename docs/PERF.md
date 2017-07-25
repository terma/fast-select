Below you can find comparision of fast-select with other databases by 3 different queries. Table has [24 columns](https://github.com/terma/fast-select/blob/master/src/main/java/com/github/terma/fastselect/demo/DemoData.java)

* [fast-select vs H2](#fast-select-vs-h2)
* [fast-select vs MongoDB](#fast-select-vs-mongodb)
* [fast-select vs Spark Draft](#fast-select-vs-spark-draft)
* [fast-select vs Apache Impala](#fast-select-vs-apache-impala)
* [fast-select vs Redis](#fast-select-vs-redis)

# fast-select vs H2

H2 setup:
* Transaction log was truned off to speed up

Result:
```
Benchmark                               (blockSize)    (engine)  (volume)  Mode  Cnt     Score  Error  Units
first 25 where (11 + 6 con + sorting)                        H2   1000000  avgt       1422.091         ms/op
first 25 where (11 + 6 con + sorting)          1000  FastSelect   1000000  avgt         35.328         ms/op
group 2 columns and where (10k + 6)                          H2   1000000  avgt       1767.386         ms/op
group 2 columns and where (10k + 6)            1000  FastSelect   1000000  avgt         22.685         ms/op
group 2 columns and where (11 + 6 con)                       H2   1000000  avgt        281.696         ms/op
group 2 columns and where (11 + 6 con)         1000  FastSelect   1000000  avgt         11.773         ms/op
```

# fast-select vs MongoDB

#### Env:
* Mac Air 13" i5 1.4HGz RAM 8Gb SSD 110Gb

#### fast-select setup:
* ```-Xmx3g```
* [Test Cases](https://github.com/terma/fast-select/blob/master/src/test/java/com/github/terma/fastselect/benchmark/PlayerFastSelect.java)

#### MongoDB setup:
* Single node
* Query profiling disabled and ```--slowms``` increased to ```1min```
* Index for each column to query, no compound indexes as not my use case
* [Test Cases](https://github.com/terma/fast-select/blob/master/src/test/java/com/github/terma/fastselect/benchmark/PlayerMongoDb.java)

#### Raw:
```
Benchmark                              (engine)  (volume)  Mode  Cnt      Score   Error  Units
groupByWhereHugeIn                      MongoDb   1000000  avgt          87.820          ms/op
groupByWhereHugeIn                   FastSelect   1000000  avgt           0.034          ms/op
groupByWhereIn                          MongoDb   1000000  avgt        2396.010          ms/op
groupByWhereIn                       FastSelect   1000000  avgt          21.943          ms/op
groupByWhereManyHugeIn                  MongoDb   1000000  avgt         151.106          ms/op
groupByWhereManyHugeIn               FastSelect   1000000  avgt           0.084          ms/op
groupByWhereManyIn                      MongoDb   1000000  avgt        1821.709          ms/op
groupByWhereManyIn                   FastSelect   1000000  avgt          15.903          ms/op
groupByWhereManyIn50Threads             MongoDb   1000000  avgt       45994.938          ms/op
groupByWhereManyIn50Threads          FastSelect   1000000  avgt         346.971          ms/op
groupByWhereManyIn5Threads              MongoDb   1000000  avgt        4430.695          ms/op
groupByWhereManyIn5Threads           FastSelect   1000000  avgt          36.367          ms/op
groupByWhereManyRange                   MongoDb   1000000  avgt        1340.807          ms/op
groupByWhereManyRange                FastSelect   1000000  avgt          25.019          ms/op
groupByWhereManySimple                  MongoDb   1000000  avgt           0.517          ms/op
groupByWhereManySimple               FastSelect   1000000  avgt           0.043          ms/op
groupByWhereRange                       MongoDb   1000000  avgt        1600.680          ms/op
groupByWhereRange                    FastSelect   1000000  avgt          21.350          ms/op
groupByWhereSimple                      MongoDb   1000000  avgt         590.481          ms/op
groupByWhereSimple                   FastSelect   1000000  avgt           7.329          ms/op
groupByWhereSimpleRangeInStringLike     MongoDb   1000000  avgt         405.172          ms/op
groupByWhereSimpleRangeInStringLike  FastSelect   1000000  avgt           8.549          ms/op
groupByWhereString                      MongoDb   1000000  avgt           0.398          ms/op
groupByWhereString                   FastSelect   1000000  avgt          49.741          ms/op
groupByWhereStringLike                  MongoDb   1000000  avgt        1363.704          ms/op
groupByWhereStringLike               FastSelect   1000000  avgt         156.684          ms/op
selectLimit                             MongoDb   1000000  avgt           0.835          ms/op
selectLimit                          FastSelect   1000000  avgt           0.031          ms/op
selectOrderByLimit                      MongoDb   1000000  avgt           0.817          ms/op
selectOrderByLimit                   FastSelect   1000000  avgt          28.271          ms/op
```

#### Threads:
![Chart](https://github.com/terma/fast-select/raw/master/docs/fast-select-vs-mongo-db.png)

#### Summary:
* 28 cases from 30 fast-select faster MongoDb in ```100 times```
* ```groupByWhereString``` case which should be fixed by https://github.com/terma/fast-select/pull/22
* ```selectOrderByLimit``` is interesting case as well need to clarify

# fast-select vs Spark (Draft)

Just quick check. Due to luck of Spark knowledge it could be 
way to improve Spark result. In progress. If you see any mistakes please ping me.

#### Env:
* Mac Air 13" i5 1.4HGz RAM 8Gb SSD 110Gb

#### fast-select setup:
* ```-Xmx3g```
* [Test Cases](https://github.com/terma/fast-select/blob/master/src/test/java/com/github/terma/fastselect/benchmark/PlayerFastSelect.java)

#### Spark setup:
* same machine
* 1 worker, 4 cores
* ```7 Gb``` for Spark
* data class ```case class T1(prr: Byte, prg: Short, tr: String)```
* RDD query ```val t1rdd = sc.makeRDD(0 until 1000000 map {i => T1((i % 6).toByte, (i % 100).toShort, s"String like value ${(i % 50000)}") }).cache```
* DF query ```val t1rdd = sc.makeRDD(0 until 1000000 map {i => T1((i % 6).toByte, (i % 100).toShort, s"String like value ${(i % 50000)}") }).toDF().cache``` 

#### Spark Measure Function

```scala
def measure(body: => Unit): Unit = {
  var h = 3
  0 until h foreach {_ => body}
  val s = System.currentTimeMillis()
  val r = 10
  0 until r foreach {_ => body}
  println(s"took avg ${(System.currentTimeMillis() - s)/r} ms/op (runs $r)");
}
```

#### Results:
```
Benchmark               (partitions)    (engine)  (volume)  Score  Units
groupByWhereStringLike             4   Spark RDD   1000000   2500  ms/op
groupByWhereStringLike             4    Spark DF   1000000   2734  ms/op
groupByWhereStringLike             1    Spark DF   1000000   3812  ms/op
groupByWhereStringLike           N/A  FastSelect   1000000    141  ms/op

```

# fast-select vs Apache Impala

#### Env
* Demo VM from http://kudu.apache.org/docs/quickstart.html on top of Mac Air 13" i5 1.4HGz RAM 8Gb SSD 110Gb
* No additional changes for VM

#### Impala Setup

* Impala Daemon version ```2.8.0```
* On top of Hadoop
* Data stored in [Parquet](https://parquet.apache.org) within Impala (not Hadoop)
 * CSV table tested as well but in general in two times slower than Parquet
* It's hard to install JDBC driver for Impala so testing was done from Impala console

#### Raw 

```
Benchmark                            (blockSize)    (engine)  (volume)  Mode  Cnt    Score   Error  Units
groupByWhereSimple                          1000  FastSelect   1000000  avgt        11.780          ms/op
groupByWhereSimple                                    Impala   1000000  avgt       350.000          ms/op
groupByWhereManySimple                      1000  FastSelect   1000000  avgt         0.042          ms/op
groupByWhereManySimple                                Impala   1000000  avgt       350.000          ms/op
groupByWhereIn                              1000  FastSelect   1000000  avgt        31.498          ms/op
groupByWhereIn                                        Impala   1000000  avgt       350.000          ms/op
groupByWhereManyIn                          1000  FastSelect   1000000  avgt        17.381          ms/op
groupByWhereManyIn                                    Impala   1000000  avgt       340.000          ms/op
groupByWhereRange                           1000  FastSelect   1000000  avgt        26.126          ms/op
groupByWhereRange                                     Impala   1000000  avgt       340.000          ms/op
groupByWhereManyRange                       1000  FastSelect   1000000  avgt        21.836          ms/op
groupByWhereManyRange                                 Impala   1000000  avgt       250.000          ms/op
groupByWhereString                          1000  FastSelect   1000000  avgt        42.969          ms/op
groupByWhereString                                    Impala   1000000  avgt       320.000          ms/op
groupByWhereStringLike                      1000  FastSelect   1000000  avgt       141.119          ms/op
groupByWhereStringLike                                Impala   1000000  avgt       220.000          ms/op
selectLimit                                 1000  FastSelect   1000000  avgt         0.765          ms/op
selectLimit                                           Impala   1000000  avgt       230.000          ms/op
selectOrderByLimit                          1000  FastSelect   1000000  avgt        43.393          ms/op
selectOrderByLimit                                    Impala   1000000  avgt       800.000          ms/op
```

#### Summary

* _Research in progress_
* Currently all test cases fast-select faster Apache Impala in ```10-30 times```
* Much faster compare to MongoDB
* I expect much better performance according to http://cidrdb.org/cidr2015/Papers/CIDR15_Paper28.pdf 
* My initial idea was that ```200-300ms``` is data access price however it's not because ```count(*)``` takes only ```160ms```
Need more investigation.


# fast-select vs Redis

_Testing in progress will be soon..._
