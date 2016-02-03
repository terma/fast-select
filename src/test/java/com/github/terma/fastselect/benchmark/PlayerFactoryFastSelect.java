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
import com.github.terma.fastselect.demo.DemoData;

import java.util.List;

class PlayerFactoryFastSelect implements PlayerFactory<DemoData> {

    private FastSelect<DemoData> fastSelect = new FastSelect<>(DemoData.class);

    @Override
    public void addData(List<DemoData> data) throws Exception {
        fastSelect.addAll(data);
    }

    @Override
    public Player createPlayer() throws Exception {
        return new PlayerFastSelect(fastSelect);
    }
}
