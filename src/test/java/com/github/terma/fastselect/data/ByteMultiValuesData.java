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

import java.util.Arrays;

public class ByteMultiValuesData {

    public byte[] a;

    @SuppressWarnings("unused")
    public ByteMultiValuesData() {
    }

    public ByteMultiValuesData(byte[] value) {
        this.a = value;
    }

    @Override
    public String toString() {
        return "ByteMultiValues{" + "a=" + Arrays.toString(a) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteMultiValuesData that = (ByteMultiValuesData) o;
        return Arrays.equals(a, that.a);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(a);
    }
}
