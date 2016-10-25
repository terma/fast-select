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
