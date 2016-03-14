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

package com.github.terma.fastselect.jmx;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.FastSelectBuilder;
import com.github.terma.fastselect.data.Data;
import com.github.terma.fastselect.demo.DemoData;
import junit.framework.Assert;
import org.junit.Test;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;

public class FastSelectMXBeanImplTest {

    private FastSelect<DemoData> fastSelect = new FastSelectBuilder<>(DemoData.class).create();
    private FastSelectMXBean fastSelectMXBean = new FastSelectMXBeanImpl(fastSelect);

    @Test
    public void successfullyRegisterInMBServer() throws JMException {
        final ObjectName objectName = new ObjectName(":type=test");

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(fastSelectMXBean, objectName);
        mbs.unregisterMBean(objectName);
    }

    @Test
    public void provideSize() {
        Assert.assertEquals(0, fastSelectMXBean.getSize());
        fastSelect.addAll(Collections.singletonList(new DemoData()));
        Assert.assertEquals(1, fastSelectMXBean.getSize());
    }

    @Test
    public void provideAllocatedSize() {
        Assert.assertEquals(Data.DEFAULT_SIZE, fastSelectMXBean.getAllocatedSize());
        for (int i = 0; i < 50; i++) fastSelect.addAll(Collections.singletonList(new DemoData()));
        Assert.assertEquals(300017, fastSelectMXBean.getAllocatedSize());
    }

    @Test
    public void provideMem() {
        Assert.assertEquals(2020, fastSelectMXBean.getMemInBytes());
        Assert.assertEquals(0, fastSelectMXBean.getMemInMb());
        Assert.assertEquals(0, fastSelectMXBean.getMemInGb());

        for (int i = 0; i < 50; i++) fastSelect.addAll(Collections.singletonList(new DemoData()));

        Assert.assertEquals(20102043, fastSelectMXBean.getMemInBytes());
        Assert.assertEquals(19, fastSelectMXBean.getMemInMb());
        Assert.assertEquals(0, fastSelectMXBean.getMemInGb());
    }

    @Test
    public void provideColumns() {
        List<ColumnBean> columnBeans = fastSelectMXBean.getColumns();

        Assert.assertEquals(23, columnBeans.size());
        Assert.assertEquals("ui", columnBeans.get(0).getName());
        Assert.assertEquals("java.lang.String", columnBeans.get(0).getType());
        Assert.assertEquals(16, columnBeans.get(0).getAllocatedSize());
        Assert.assertEquals(0, columnBeans.get(0).getSize());
        Assert.assertEquals(168, columnBeans.get(0).getMemInBytes());
        Assert.assertEquals(0, columnBeans.get(0).getMemInMb());
        Assert.assertEquals(0, columnBeans.get(0).getMemInGb());
    }

}
