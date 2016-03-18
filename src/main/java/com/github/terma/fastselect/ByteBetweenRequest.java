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

package com.github.terma.fastselect;

import com.github.terma.fastselect.data.ByteData;

/**
 * Math's analog is "a to []". Include min and max.
 * For equal select use {@link ByteRequest}
 */
@SuppressWarnings("WeakerAccess")
public class ByteBetweenRequest extends AbstractRequest {

    private final byte min;
    private final byte max;
    private byte[] data;

    public ByteBetweenRequest(String name, byte min, byte max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean checkBlock(Range range) {
        return range.max >= min && range.min <= max;
    }

    @Override
    public boolean checkValue(int position) {
        final byte value = data[position];
        return value >= min && value <= max;
    }

    @Override
    public void prepare() {
        // caching
        data = ((ByteData) column.data).data;
    }

    @Override
    public String toString() {
        return "{name: '" + name + "', min: " + min + ", max: " + max + '}';
    }

}
