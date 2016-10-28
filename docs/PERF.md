Below you can find comparision of fast-select with other databases by 3 different queries. Table has [24 columns](https://github.com/terma/fast-select/blob/master/src/main/java/com/github/terma/fastselect/demo/DemoData.java)

* [fast-select vs H2](#fast-select-vs-h2)
* [fast-select vs MongoDB](#fast-select-vs-mongodb)

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

# fast-select vs MongoDB, in progress...

#### Env:
* Mac Air 13" i5 1.4HGz RAM 8Gb SSD 110Gb

#### MongoDB setup:
* Single node
* Query profiling disabled and ```--slowms``` increased to ```1min```
* Index for each column to query, no compound indexes as not my use case

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
