package com.github.terma.fastselect;

import com.github.terma.fastselect.data.Data;
import com.github.terma.fastselect.data.StringCompressedByteData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Table {

    private final Map<String, Data> datas = new HashMap<>();

    public Table() {
    }

    public Table(Table table, String... columns) {
        for (String column : columns) datas.put(column, table.get(column));
    }

    @SuppressWarnings("unchecked")
    public <T extends Data> T get(String column) {
        T data = (T) datas.get(column);
        if (data == null) throw new IllegalArgumentException("???");
        return data;
    }

    public int size() {
        return datas.isEmpty() ? 0 : datas.entrySet().iterator().next().getValue().size();
    }

    public StringCompressedByteData addStringCompressedByteData(String column, int inc) {
        StringCompressedByteData data = new StringCompressedByteData(inc);
        datas.put(column, data);
        return data;
    }

    public static void main(String[] args) {
        ExecutorService executorService = null;

        Future<Table> iatDetailsStage = executorService.submit(new Callable<Table>() {
            @Override
            public Table call() throws Exception {
                // todo load iat details stage
                return null;
            }
        });

    }

}
