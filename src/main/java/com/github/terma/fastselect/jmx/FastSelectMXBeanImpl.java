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

package com.github.terma.fastselect.jmx;

import com.github.terma.fastselect.FastSelect;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class FastSelectMXBeanImpl implements FastSelectMXBean {

    private final FastSelect fastSelect;

    public FastSelectMXBeanImpl(FastSelect fastSelect) {
        this.fastSelect = fastSelect;
    }

    public static FastSelectMXBean register(final String name, final FastSelect fastSelect) throws JMException {
        final ObjectName objectName = new ObjectName(FastSelect.class.getPackage().getName() + ":type=" + name);
        FastSelectMXBean fastSelectMXBean = new FastSelectMXBeanImpl(fastSelect);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(fastSelectMXBean, objectName);

        return fastSelectMXBean;
    }

    @Override
    public int getSize() {
        return fastSelect.size();
    }

    @Override
    public int getAllocatedSize() {
        return fastSelect.allocatedSize();
    }

    @Override
    public long getMemInBytes() {
        return fastSelect.mem();
    }

    @Override
    public long getMemInMb() {
        return fastSelect.mem() / 1024 / 1024;
    }

    @Override
    public long getMemInGb() {
        return fastSelect.mem() / 1024 / 1024 / 1024;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ColumnBean> getColumns() {
        List<ColumnBean> columnBeans = new ArrayList<>();

        for (Object column : fastSelect.getColumns()) {
            FastSelect.Column column1 = (FastSelect.Column) column;
            columnBeans.add(new ColumnBean(column1.name, column1.getType().getName(),
                    column1.size(), column1.allocatedSize(), column1.mem()));
        }
        return columnBeans;
    }

}
