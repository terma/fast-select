/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.fastselect.demo.DemoData;
import junit.framework.Assert;
import org.junit.Test;

import static java.util.Collections.singletonList;

public class FastSelectBuilderTest {

    @Test
    public void createDefaultFastSelect() {
        FastSelect<DemoData> fastSelect = new FastSelectBuilder<>(DemoData.class).create();
        Assert.assertEquals(FastSelectBuilder.DEFAULT_INC, fastSelect.getColumns().get(0).data.inc());
        Assert.assertEquals(FastSelectBuilder.DEFAULT_BLOCK_SIZE, fastSelect.dataBlockSize());
    }

    @Test
    public void createFastSelectWithCustomBlockSize() {
        FastSelect<DemoData> fastSelect = new FastSelectBuilder<>(DemoData.class).blockSize(12).create();
        Assert.assertEquals(12, fastSelect.dataBlockSize());
    }

    @Test
    public void createFastSelectWithCustomInc() {
        FastSelect<DemoData> fastSelect = new FastSelectBuilder<>(DemoData.class).inc(99).create();
        Assert.assertEquals(99, fastSelect.getColumns().get(0).data.inc());
    }

    @Test
    public void createFastSelectWithCustomColumns() {
        FastSelect.Column column = new FastSelect.Column("prg", byte.class, 12);
        FastSelect<DemoData> fastSelect = new FastSelectBuilder<>(DemoData.class).columns(singletonList(column)).create();
        Assert.assertEquals(singletonList(column), fastSelect.getColumns());
    }

}
