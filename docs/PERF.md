Below you can find comparision of fast-select with other databases by 3 different queries. Table has [24 columns](https://github.com/terma/fast-select/blob/master/src/main/java/com/github/terma/fastselect/demo/DemoData.java)

* [fast-select vs H2](#fast-select-vs-h2)
* [fast-select vs MongoDB](#fast-select-vs-mongodb)

## fast-select vs H2

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

## fast-select vs MongoDB

MongoDB setup:
* Single node
* Index for each column to query
* _Note: I didn't find different between test with indexes and without. Need to clarify that question._

Result:
```
Benchmark                                           (blockSize)    (engine)  (volume)  Mode  Cnt      Score   Error  Units
group 2 columns and where (11 + 6 con)                              MongoDb   1000000  avgt        1558.917          ms/op
group 2 columns and where (11 + 6 con)                     1000  FastSelect   1000000  avgt           7.692          ms/op
group 2 columns and where (11 + 6 con) x50 threads                  MongoDb   1000000  avgt       39526.173          ms/op
group 2 columns and where (11 + 6 con) x50 threads         1000  FastSelect   1000000  avgt         152.550          ms/op
group 2 columns and where (11 + 6 con)  x5 threads                  MongoDb   1000000  avgt        4150.435          ms/op
group 2 columns and where (11 + 6 con)  x5 threads         1000  FastSelect   1000000  avgt          20.384          ms/op
```

![Image of Yaktocat](docs/fast-select-vs-mongo-db.png)
