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

import java.util.Objects;

public class LongShortData {

    public long long1;
    public short short1;

    // empty constructor for database to be able restore object
    public LongShortData() {
        this(0, (byte) 0);
    }

    public LongShortData(long long1, short short1) {
        this.long1 = long1;
        this.short1 = short1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongShortData that = (LongShortData) o;
        return long1 == that.long1 &&
                short1 == that.short1;
    }

    @Override
    public String toString() {
        return "TestLongShort {long1: " + long1 + ", short1: " + short1 + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(long1, short1);
    }

}
