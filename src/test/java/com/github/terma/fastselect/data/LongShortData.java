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

    public long longValue;
    public short shortValue;

    // empty constructor for database to be able restore object
    public LongShortData() {
        this(0, (byte) 0);
    }

    public LongShortData(long longValue, short shortValue) {
        this.longValue = longValue;
        this.shortValue = shortValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongShortData that = (LongShortData) o;
        return longValue == that.longValue &&
                shortValue == that.shortValue;
    }

    @Override
    public String toString() {
        return "TestLongShort {longValue: " + longValue + ", shortValue: " + shortValue + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(longValue, shortValue);
    }

}
