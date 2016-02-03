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

import com.github.terma.fastselect.demo.DemoData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class PlayerFactoryH2 implements PlayerFactory<DemoData> {

    private final Connection connection;

    public PlayerFactoryH2() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0");
            connection.createStatement().execute("create table tb (" +
                    "prg tinyint, csg tinyint, prr tinyint, csr tinyint, bsid int" +
                    ");");
            connection.createStatement().execute("create index on tb (prg);");
            connection.createStatement().execute("create index on tb (csg);");
            connection.createStatement().execute("create index on tb (prr);");
            connection.createStatement().execute("create index on tb (csr);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addData(List<DemoData> data) throws Exception {
        // todo add data
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into " +
                        "tb (prg, csg, prr, csr, bsid) " +
                        "values (?, ?, ?, ?, ?);");
        for (DemoData demoData : data) {
            preparedStatement.setByte(1, demoData.prg);
            preparedStatement.setByte(2, demoData.csg);
            preparedStatement.setByte(3, demoData.prr);
            preparedStatement.setByte(4, demoData.csr);
            preparedStatement.setInt(5, demoData.bsid);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    @Override
    public Player createPlayer() throws Exception {
        connection.close();
        return new PlayerH2();
    }
}
