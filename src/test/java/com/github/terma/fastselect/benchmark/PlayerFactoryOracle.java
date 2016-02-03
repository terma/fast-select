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

class PlayerFactoryOracle implements PlayerFactory<DemoData> {

    private final Connection connection;

    public PlayerFactoryOracle() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@192.168.56.101:1521:cdb1", "sys as sysdba", "oracle");
            try {
                connection.createStatement().execute("drop table test1");
            } catch (Exception e) {
            }
            connection.createStatement().execute("create table test1 (prg int, csg int, prr int, csr int, bsid int)");
            connection.createStatement().execute("create bitmap index prg_i on test1 (prg)");
            connection.createStatement().execute("create bitmap index prr_i on test1 (prr)");
            connection.createStatement().execute("alter table test1 inmemory");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addData(List<DemoData> data) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into " +
                        "test1 (prg, csg, prr, csr, bsid) " +
                        "values (?, ?, ?, ?, ?)");
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
        return new PlayerOracle();
    }
}
