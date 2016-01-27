package com.github.terma.fastselect.benchmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class PlayerH2 implements Player {

    @Override
    public Object playGroupByGAndR() throws Exception {
        int c;
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                    "select count(*) from tb where prg in (1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89) and prr in (1, 2, 3, 4, 5, 6)")) {
                resultSet.next();
                c = resultSet.getInt(1);
            }
        }
        return c;
    }

}
