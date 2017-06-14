[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/github/terma/fast-select/badge.svg?branch=master)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

## fast-select

Compact in-memory read-only storage with lock free ultra-fast quering by any attributes under Apache 2.0 license.

### Key Properties

* Compact 
  * No java object overhead (in avg ```x10``` less than object representation)
  * Compact string representation (```UTF-8``` instead of Java ```UTF-16```)
  * String data compression
  * Small metadata footprint (no indexes overhead)
* Fast
  * All dimension available for search
  * Using data statistic to avoid full scan
  * Column oriented
  * Thread safe and lock free
* Support fast save/load to/from disk [details](USECASES.md)
* Small jar file
* Apache 2.0

### Use Cases

* Speed up analytical quering by caching main data in compact and query optimized way instead of using expensive solution [details](USECASES.md#speed-up-analytic)
* Separate ETL and analytic load by keeping main data optimized for processing and add compact model optimizing for quering [details](USECASES.md#separate-processing-and-analytic)
* Sub second quering of historical data by loading portion of data on demand in a seconds [details](USECASES.md#speed-up-history-analytic)

### Details

* [Architecture](docs/ARHI.md)
* [Use Cases](USECASES.md)
* [How To Use](docs/HOWTOUSE.md)
* [Performance](docs/PERF.md)
* [Javadoc](http://terma.github.io/fast-select/)

