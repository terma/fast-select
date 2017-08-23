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

package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PlayerH2 implements Player {

    private static void groupBy(Map<Integer, Map<Integer, Integer>> g, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Integer prg = resultSet.getInt(1);
            Integer prr = resultSet.getInt(2);
            Integer grc = resultSet.getInt(3);

            Map<Integer, Integer> r = g.get(prg);
            if (r == null) {
                r = new HashMap<>();
                g.put(prg, r);
            }

            Integer c = r.get(prr);
            if (c == null) c = 0;
            r.put(prr, c + grc);
        }
    }

    @Override
    public Object groupByWhereSimple() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereManySimple() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereIn() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereHugeIn() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereManyIn() throws Exception {
        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select prg, prr, count(*) from tb where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6) group by prg, prr")) {
                groupBy(g, resultSet);
            }
        }
        return g;
    }

    @Override
    public Object groupByWhereManyHugeIn() throws Exception {
        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select bsid, prr, count(*) from tb where prr in (1, 2, 3, 4, 5, 6) and bsid in (" + DemoData.joinByComma(DemoUtils.getBsIds()) + ") group by bsid, prr")) {
                groupBy(g, resultSet);
            }
        }
        return g;
    }

    @Override
    public Object groupByWhereRange() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereManyRange() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereStringLike() throws Exception {
        return null;
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
        return null;
    }

    @Override
    public Object groupByWhereManyString() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereSimpleRangeInStringLike() throws Exception {
        return null;
    }

    @Override
    public Object selectLimit() throws Exception {
        return null;
    }

    @Override
    public Object selectOrderByLimit() throws Exception {
        List<Object> r = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select * from tb where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6) order by prr limit 25")) {
                while (resultSet.next()) {
                    final List<Object> c = new ArrayList<>();
                    final int columnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i < columnCount; i++) {
                        c.add(resultSet.getObject(i));
                    }
                    r.add(c);
                }
            }
        }
        return r.size();
    }

}
