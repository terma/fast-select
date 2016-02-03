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

import com.github.terma.fastselect.FastSelect;
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
    public Object playGroupByGAndR() {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("prg"),
                columnsByNames.get("prr")
        );
        fastSelect.select(DemoUtils.whereGAndR(), callback);
        return callback.getCounters();
    }

    @Override
    public Object playGroupByBsIdAndR() {
        final Map<String, FastSelect.Column> columnsByNames = fastSelect.getColumnsByNames();
        final MultiGroupCountCallback callback = new MultiGroupCountCallback(
                columnsByNames.get("bsid"),
                columnsByNames.get("prr")
        );
        fastSelect.select(DemoUtils.whereBsIdAndR(), callback);
        return callback.getCounters();
    }

    @Override
    public Object playDetailsByGAndRAndSorting() throws Exception {
        ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
        fastSelect.selectAndSort(DemoUtils.whereGAndR(), callback, "prr");
        return callback.getResult().size();
    }
}
