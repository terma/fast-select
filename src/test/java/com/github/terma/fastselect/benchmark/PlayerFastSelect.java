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

package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.*;
import com.github.terma.fastselect.callbacks.ListLimitCallback;
import com.github.terma.fastselect.callbacks.MultiGroupCountCallback;
import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;

import java.util.Map;

class PlayerFastSelect implements Player {

    private final FastSelect<DemoData> fastSelect;

    PlayerFastSelect(final FastSelect<DemoData> fastSelect) {
        this.fastSelect = fastSelect;
    }

    @Override
    public Object groupByWhereSimple() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{new ByteRequest("prr", 1)}, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereManySimple() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{
                new ByteRequest("prr", 1),
                new ByteRequest("prg", 89),
                new ByteRequest("csg", 50)
        }, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereIn() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{new ByteRequest("prr", DemoData.SCALAR_IN_2)}, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereHugeIn() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{new IntRequest("bsid", DemoUtils.HUGE_SCALAR_IN)}, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereManyIn() {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(DemoUtils.whereGAndR(), callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereManyHugeIn() {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(DemoUtils.whereBsIdAndR(), callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereRange() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{new LongBetweenRequest("vlc", DemoData.RANGE_LEFT, DemoData.RANGE_RIGHT)}, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereManyRange() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{
                new LongBetweenRequest("vlc", DemoData.RANGE_LEFT, DemoData.RANGE_RIGHT),
                new LongBetweenRequest("vch", DemoData.RANGE_LEFT, DemoData.RANGE_RIGHT)
        }, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereStringLike() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{
                new StringLikeRequest("tr", DemoData.STRING_LIKE),
        }, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereSpareStringLike() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereManyStringLike() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereString() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{
                new StringRequest("tr", DemoData.STRING_LIKE),
        }, callback);
        return callback.getCounters();
    }

    @Override
    public Object groupByWhereManyString() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereSimpleRangeInStringLike() throws Exception {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(new Request[]{
                new ByteRequest("prr", 1),
                new ByteRequest("prg", DemoData.SCALAR_IN_1),
                new LongBetweenRequest("vlc", DemoData.RANGE_LEFT, DemoData.RANGE_RIGHT),
                new StringLikeRequest("tr", DemoData.STRING_LIKE),
        }, callback);
        return callback.getCounters();
    }

    @Override
    public Object selectLimit() throws Exception {
        ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
        fastSelect.select(callback, (Request[]) DemoUtils.whereGAndR());
        return callback.getResult().size();
    }

    @Override
    public Object selectOrderByLimit() throws Exception {
        ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
        fastSelect.selectAndSort(DemoUtils.whereGAndR(), callback, "prr");
        return callback.getResult().size();
    }
}
