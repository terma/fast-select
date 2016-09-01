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

import com.github.terma.fastselect.data.StringData;
import com.github.terma.fastselect.utils.Utf8Utils;

import java.util.Map;

/**
 * SQL analog <code>where lowerCase(STRING_FIELD) like lowerCase('%SUBSTRING%')</code>
 * <p>
 * Implemented as full scan
 * <p>
 * Case sensitive like select use {@link StringLikeRequest}
 * Case sensitive exact select use {@link StringRequest}
 */
@SuppressWarnings("WeakerAccess")
public class StringNoCaseLikeRequest extends ColumnRequest {

    private final String like;
    private StringData data;

    @Deprecated
    public StringNoCaseLikeRequest(String name, String like) {
        super(name);

        if (like == null) throw new IllegalArgumentException("Can't search null string!");
        this.like = like.toLowerCase();
    }

    public static Request create(final String columnName, final String noCaseLike) {
        final byte[] bytes = noCaseLike.getBytes();
        if (Utf8Utils.isLatinOnly(bytes)) return new LatinStringNoCaseLikeRequest(columnName, bytes);
        else return new StringNoCaseLikeRequest(columnName, noCaseLike);
    }

    @Override
    public boolean checkValue(final int position) {
        final String value = (String) data.get(position);
        return value.toLowerCase().contains(like);
    }

    @Override
    public void prepare(final Map<String, FastSelect.Column> columnByNames) {
        super.prepare(columnByNames);
        data = (StringData) column.data;
    }

    @Override
    public String toString() {
        return "StringNoCaseLikeRequest {name: " + name + ", like: " + like + '}';
    }

}
