package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.demo.DemoData;

import java.sql.*;
import java.util.List;

public class PlayerFactoryH2 implements PlayerFactory<DemoData> {

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
