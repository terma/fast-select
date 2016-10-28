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
import com.github.terma.fastselect.demo.DemoUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.SQLException;
import java.util.*;

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
    public Object groupByWhereSimple() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prr\": 1}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereManySimple() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prr\": 1, \"prg\": 89, \"csg\": 50}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereIn() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prr\": {$in: [" + DemoData.SCALAR_IN_2_AS_STRING + "]}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereHugeIn() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"bsid\": {$in: [" + DemoData.joinByComma(DemoUtils.getBsIds()) + "]}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereManyIn() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prg\": {$in: [" + DemoData.SCALAR_IN_1_AS_STRING + "]}, \"prr\": {$in: [" + DemoData.SCALAR_IN_2_AS_STRING + "]}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereManyHugeIn() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"prr\": {$in: [" + DemoData.SCALAR_IN_1_AS_STRING + "]}, \"bsid\": {$in: [" + DemoData.joinByComma(DemoUtils.getBsIds()) + "]}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereRange() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {\"vlc\": {$gt: " + DemoData.RANGE_LEFT + ", $lt: " + DemoData.RANGE_RIGHT + "}}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereManyRange() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {" +
                                "$and: [" +
                                "{\"vlc\": {$gt: " + DemoData.RANGE_LEFT + ", $lt: " + DemoData.RANGE_RIGHT + "}}, {\"vch\": {$gt: " + DemoData.RANGE_LEFT + ", $lt: " + DemoData.RANGE_RIGHT + "}}" +
                                "]}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereStringLike() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {tr: /" + DemoData.STRING_LIKE + "/}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
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
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {tr: \"" + DemoData.STRING_LIKE + "\"}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object groupByWhereManyString() throws Exception {
        return null;
    }

    @Override
    public Object groupByWhereSimpleRangeInStringLike() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Document.parse("{$match: {$and: [{prr: 1}, {prg: {$in: [" + DemoData.SCALAR_IN_1_AS_STRING + "]}}, {vlc: {$gt: " + DemoData.RANGE_LEFT + ", $lt: " + DemoData.RANGE_RIGHT + "}}, {tr: /" + DemoData.STRING_LIKE + "/}]}}"),
                        Document.parse("{$group: {_id: {prg: \"$prg\", prr: \"$prr\"}, count: {$sum: 1}}}")
                )
        ).iterator();

        Map<Integer, Map<Integer, Integer>> g = new HashMap<>();
        groupBy(g, cursor);
        cursor.close();
        return g;
    }

    @Override
    public Object selectLimit() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.find(
                Document.parse("{\"prg\": {$in: [" + DemoData.SCALAR_IN_1_AS_STRING + "]}, \"prr\": {$in: [" + DemoData.SCALAR_IN_2_AS_STRING + "]}}")
        ).limit(25).iterator();

        List<Object> r = new ArrayList<>();
        while (cursor.hasNext()) {
            r.add(cursor.next());
        }

        cursor.close();
        return r.size();
    }

    @Override
    public Object selectOrderByLimit() throws Exception {
        MongoCollection collection = database.getCollection("myCollection");
        MongoCursor<Document> cursor = collection.find(
                Document.parse("{\"prg\": {$in: [" + DemoData.SCALAR_IN_1_AS_STRING + "]}, \"prr\": {$in: [" + DemoData.SCALAR_IN_2_AS_STRING + "]}}")
        ).sort(Document.parse("{prr: 1}")).limit(25).iterator();

        List<Object> r = new ArrayList<>();
        while (cursor.hasNext()) {
            r.add(cursor.next());
        }

        cursor.close();
        return r.size();
    }
}
