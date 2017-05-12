[![Build Status](https://travis-ci.org/terma/fast-select.svg?branch=start)](https://travis-ci.org/terma/fast-select)
[![Coverage Status](https://coveralls.io/repos/github/terma/fast-select/badge.svg?branch=master)](https://coveralls.io/github/terma/fast-select?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.terma/fast-select/)

## fast-select

Compact in-memory read-only storage with lock free ultra-fast quering by any attributes under Apache 2.0 license.

### Compact

* No java object overhead
* Compact string representation (```UTF-8``` instead of Java ```UTF-16```)
* Persistance mode to restore storage from disk [details](USECASES.md)

### Ultra fast

* Thread safe and lock free
* Using data statistic to avoid full scan
* Column oriented

### More

* Small jar file
* Apache 2.0

### Details

* [Architecture](docs/ARHI.md)
* [Use Cases](docs/USECASES.md)
* [How To Use](docs/HOWTOUSE.md)
* [Performance](docs/PERF.md)
* [Javadoc](http://terma.github.io/fast-select/)

