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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

class PlayerOracle implements Player {

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
        int c;
        try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.56.101:1521:cdb1", "sys as sysdba", "oracle")) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "select count(*) from test1 where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6)")) {
                resultSet.next();
                c = resultSet.getInt(1);
            }
        }
        return c;
    }

    @Override
    public Object groupByWhereManyHugeIn() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
}
