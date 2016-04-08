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

/**
 * SQL analog <code>where STRING_FIELD like '%SUBSTRING%'</code>
 *
 * @see StringNoCaseLikeRequest
 * @see StringRequest
 */
@SuppressWarnings("WeakerAccess")
public class StringLikeRequest extends ColumnRequest {

    private final String like;

    public StringLikeRequest(String name, String like) {
        super(name);
        this.like = like;
    }

    @Override
    public boolean checkValue(int position) {
        StringData data = (StringData) column.data;
        String value = (String) data.get(position);
        return value.contains(like);
    }

    @Override
    public String toString() {
        return "StringLikeRequest {name: '" + name + "', like: '" + like + "'}";
    }

}
