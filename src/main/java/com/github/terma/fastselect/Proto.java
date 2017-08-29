package com.github.terma.fastselect;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Proto {

    private ExecutorService executorService = null;

    public static void main(String[] args) {
        new Proto().execute();
    }

    private static Future<Table> loadIatDetailsStage(ExecutorService executorService) {
        return executorService.submit(new Callable<Table>() {
            @Override
            public Table call() throws Exception {
                // todo load iat details stage
                return null;
            }
        });
    }

    public void execute() {
        Future<Table> iatDetailsStage = loadIatDetailsStage(executorService);
        Future<Table> aggregateStage = buildAggregateStage(iatDetailsStage);
        Table iatDetails = createIatDetails(iatDetailsStage);
    }

    private Future<Table> buildAggregateStage(Future<Table> iatDetailsStage) {
        return null;
    }

    private Table createIatDetails(Future<Table> iatDetailsStage) {
        return null;
    }

}
