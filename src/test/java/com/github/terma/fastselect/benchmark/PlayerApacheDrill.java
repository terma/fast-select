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

import com.github.terma.fastselect.demo.DemoUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PlayerApacheDrill implements Player {

    private static String joinByComma(int[] a) {
        int iMax = a.length - 1;

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.toString();
            b.append(", ");
        }
    }

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

    /*
    Connection connection = DriverManager.getConnection("jdbc:drill:drillbit=localhost:31010;schema=parquet");
            Statement statement = connection.createStatement();
     */

    @Override
    public Object playGroupByGAndR() throws Exception {
        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:drill:drillbit=localhost:31010;schema=parquet")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select prg, prr, count(*) from dfs.tmp.tb where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6) group by prg, prr")) {
                groupBy(g, resultSet);
            }
        }
        return g;
    }

    @Override
    public Object playGroupByBsIdAndR() throws Exception {
        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:drill:drillbit=localhost:31010;schema=parquet")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select bsid, prr, count(*) from dfs.tmp.tb where prr in (1, 2, 3, 4, 5, 6) and bsid in (" + joinByComma(DemoUtils.getBsIds()) + ") group by bsid, prr")) {
                groupBy(g, resultSet);
            }
        }
        return g;
    }

    @Override
    public Object playDetailsByGAndRAndSorting() throws Exception {
        List<Object> r = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:drill:drillbit=localhost:31010;schema=parquet")) {
            try (final ResultSet resultSet = connection.createStatement().executeQuery(
                    "select * from dfs.tmp.tb where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6) order by prr limit 25")) {
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
