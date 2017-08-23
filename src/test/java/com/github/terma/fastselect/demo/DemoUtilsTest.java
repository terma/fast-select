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

package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.FastSelect;
import junit.framework.Assert;
import org.junit.Test;

public class DemoUtilsTest {

    @Test
    public void createFastSelectWithDemoData() {
        FastSelect<DemoData> fastSelect = DemoUtils.createFastSelect(10, 100);

        Assert.assertNotNull(fastSelect);
        Assert.assertEquals(100, fastSelect.size());
        Assert.assertEquals(10, fastSelect.dataBlockSize());
        Assert.assertTrue(fastSelect.getColumns().size() > 0);
    }

    @Test
    public void createRequestsForDemoData() {
        Assert.assertNotNull(DemoUtils.getBsIds());
        Assert.assertNotNull(DemoUtils.whereBsIdAndR());
        Assert.assertNotNull(DemoUtils.whereGAndR());
    }

}
