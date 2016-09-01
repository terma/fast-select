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

import com.github.terma.fastselect.data.MultiByteData;
import com.github.terma.fastselect.data.StringData;
import com.github.terma.fastselect.utils.Utf8Utils;

import java.util.BitSet;
import java.util.Map;

/**
 * Not part of public API. Special class for fast search in case of only latin characters in like.
 * Please use {@link StringNoCaseLikeRequest#create(String, String)} to create proper request.
 */
@SuppressWarnings("WeakerAccess")
class LatinStringNoCaseLikeRequest extends ColumnRequest {

    private final byte[] likeBytes;

    private MultiByteData data;
    private byte[] byteData;

    public LatinStringNoCaseLikeRequest(final String name, final byte[] likeBytes) {
        super(name);
        if (likeBytes == null) throw new IllegalArgumentException("Can't search null string!");
        this.likeBytes = likeBytes;
        Utf8Utils.latinToLowerCase(this.likeBytes);
    }

    @Override
    public boolean checkBlock(final Block block) {
        BitSet bitSet = block.columnBitSets.get(column.index);
        boolean p = true;
        // todo handle case when byte is negative for non latin utf8 codes
        for (final byte value : likeBytes)
            p = p & (bitSet.get(Utf8Utils.latinToLowerCase(value)) | bitSet.get(Utf8Utils.latinToUpperCase(value)));
        return p;
    }

    @Override
    public boolean checkValue(final int position) {
        final int start = data.getDataStart(position);
        final int end = data.getDataEnd(position);
        return Utf8Utils.latinBytesContains(byteData, start, end, likeBytes);
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        data = ((StringData) column.data).getData();
        byteData = data.data.data;
    }

    @Override
    public String toString() {
        return "LatinStringNoCaseLikeRequest {name: " + name + ", like: " + new String(likeBytes) + '}';
    }

}
