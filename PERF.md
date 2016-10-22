## fast-select vs H2

[24 columns](https://github.com/terma/fast-select/blob/master/src/main/java/com/github/terma/fastselect/demo/DemoData.java)

| Benchmark                              | (blockSize)   | (engine) |(volume) |Mode |Cnt    |Score  |
| --------------------------------------:|--------------:|---------:|--------:|----:|------:|------:|
|first 25 where (11 + 6 con + sorting) |     |        H2|   1000000|  avgt     |  1422.091|          ms/op|
|first 25 where (11 + 6 con + sorting) |    1000  |FastSelect |  1000000 | avgt     |    35.328  |        ms/op|
|group 2 columns and where (10k + 6)         |          |        H2 |  1000000 | avgt    |   1767.386   |       ms/op|
|group 2 columns and where (10k + 6)         |        1000  |FastSelect |  1000000 | avgt   |      22.685    |      ms/op|
|group 2 columns and where (11 + 6 con)             |          |        H2 |  1000000 | avgt  |      281.696     |     ms/op|
|group 2 columns and where (11 + 6 con)            |        1000  |FastSelect |  1000000  |avgt |        11.773      |    ms/op|
