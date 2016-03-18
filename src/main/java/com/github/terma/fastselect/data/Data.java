/*
Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.fastselect.data;

public interface Data {

    int DEFAULT_SIZE = 16;

    long OBJECT_HEADER_BYTES = 16;
    long REFERENCE_BYTES = 8;
    long SHORT_BYTES = 2;
    long INT_BYTES = 4;
    long LONG_BYTES = 8;

    Object get(int position);

    int compare(int position1, int position2);

    void compact();

    int size();

    int allocatedSize();

    /**
     * Approximate size of Data structure in memory.
     *
     * @return - size in bytes
     */
    long mem();

    int inc();

}
