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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

class PlayerFactoryApacheDrill implements PlayerFactory<DemoData> {

    //    private String apacheDrillHome = "/Users/terma/Downloads/apache-drill-1.8.0";
    private File workDir = new File(".").getAbsoluteFile().getParentFile();
    private File csvFile = new File(workDir, "apache-drill.csv");
    private boolean dataPresent = csvFile.exists();
    private CSVPrinter csvPrinter;

    public PlayerFactoryApacheDrill() throws IOException, ClassNotFoundException {
        Class.forName("org.apache.drill.jdbc.Driver");
        if (!dataPresent) {
            Writer appendable = new FileWriter(csvFile);
            csvPrinter = CSVFormat.DEFAULT.print(appendable);
        }
    }

    @Override
    public boolean isDurable() {
        return true;
    }

    @Override
    public void startAddData() throws Exception {

    }

    @Override
    public void addData(List<DemoData> data) throws Exception {
        if (!dataPresent) {
            for (DemoData item : data) {
                csvPrinter.printRecord(
                        item.prg,
                        item.csg, item.tlg, item.tsg, item.mid,
                        item.cid,
                        item.age, item.crn, item.vlc, item.vsd,
                        item.vch, item.prr, item.csr, item.pror,
                        item.csor, item.proc, item.csoc,
                        item.bsid,
                        item.cpid,
                        item.tr,
                        item.ui,
                        item.prn,
                        item.csn);
            }
        }
    }

    @Override
    public void finishAddData() throws Exception {

    }

    @Override
    public Player createPlayer() throws Exception {
        if (!dataPresent) {
            csvPrinter.close();
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:drill:drillbit=localhost:31010;schema=parquet");
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS dfs.tmp.tb");
            statement.execute("create table dfs.tmp.tb as (select " +
                    "cast(columns[0] as INT) as prg, " +
                    "cast(columns[1] as INT) as csg, " +
                    "cast(columns[2] as INT) as tld, " +
                    "cast(columns[3] as INT) as tsg, " +
                    "cast(columns[4] as INT) as mid, " +
                    "cast(columns[5] as INT) as cid, " +
                    "cast(columns[6] as INT) as age, " +
                    "cast(columns[7] as INT) as crn, " +
                    "cast(columns[8] as BIGINT) as vlc, " +
                    "cast(columns[9] as BIGINT) as vsd, " +
                    "cast(columns[10] as BIGINT) as vch, " +
                    "cast(columns[11] as INT) as prr, " +
                    "cast(columns[12] as INT) as csr, " +
                    "cast(columns[13] as INT) as pror, " +
                    "cast(columns[14] as INT) as proc, " +
                    "cast(columns[15] as INT) as csoc, " +
                    "cast(columns[16] as INT) as bsid, " +
                    "cast(columns[17] as INT) as cpid, " +
                    "cast(columns[18] as VARCHAR) as tr, " +
                    "cast(columns[19] as VARCHAR) as ui, " +
                    "cast(columns[20] as VARCHAR) as prn, " +
                    "cast(columns[21] as VARCHAR) as csn " +
                    "from dfs.`" + csvFile.getAbsolutePath() + "`)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new PlayerApacheDrill();
    }
}
