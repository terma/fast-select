package com.github.terma.fastselect.benchmark;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerMongoDb implements Player {

    private MongoClient client = new MongoClient();
    private MongoDatabase database = client.getDatabase("test");

    private static void groupBy(Map<Integer, Map<Integer, Integer>> g, MongoCursor<Document> cursor) throws SQLException {
        while (cursor.hasNext()) {
            Document document = cursor.next();
            Document id = (Document) document.get("_id");
            Integer prg = id.getInteger("prg");
            Integer prr = id.getInteger("prr");
            Integer grc = document.getInteger("count");

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
    public Object playGroupByGAndR() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prg\": {$in: [1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89]}, \"prr\": {$in: [1, 2, 3, 4, 5, 6]}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        return g;
    }

    @Override
    public Object playGroupByBsIdAndR() throws Exception {
        return null;
    }

    @Override
    public Object playDetailsByGAndRAndSorting() throws Exception {
        return null;
    }
}
