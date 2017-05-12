### Key Points

#### Why heap array and not off heap by ```Unsafe```?

* [Heap Array faster than Unsafe](https://groups.google.com/forum/#!topic/mechanical-sympathy/k0qd7dLHFQE)
  * [Code](https://gist.github.com/rxin/ae6d7692e58c03a92861)
* https://www.slideshare.net/leventov/optimizing-arraybased-data-structures-to-the-limit
* https://blog.bramp.net/post/2015/08/27/unsafe-part-3-benchmarking-a-java-unsafearraylist/

### Data Structure

```
               Columns
                0     n
  Position 0 |----| |---| < Block 0
             | 11 | | 1 |     Start position: 0, size: 4
             |  0 | | 2 |     Column 0 > bloom filter: 0,0,0,0
             | 44 | | 3 |     Column n > range: [1,3]
             | 44 | | 3 |
             | 12 | | 0 | < Block m
             | 22 | | 0 |     Start position: 0, size: 1
           m |----| |---|     ...
```
