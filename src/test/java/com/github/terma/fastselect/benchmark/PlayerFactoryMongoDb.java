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
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * ./mongod --dbpath /Users/terma/Applications/mongodb-osx-x86_64-3.2.10/db --slowms 60000
 */
public class PlayerFactoryMongoDb implements PlayerFactory<DemoData> {

    private MongoClient client = new MongoClient();
    private MongoDatabase database = client.getDatabase("test");

    @Override
    public boolean isDurable() {
        return true;
    }

    @Override
    public void startAddData() throws Exception {
        database.getCollection("myCollection").drop();
    }

    @Override
    public void addData(List<DemoData> data) throws Exception {
        List<Document> documents = new ArrayList<>();
        for (DemoData item : data) {
            Document doc = new Document();
            doc.put("prg", item.prg);
            doc.put("prr", item.prr);
            doc.put("csg", item.csg);
            doc.put("tlg", item.tlg);
            doc.put("tsg", item.tsg);
            doc.put("mid", item.mid);
            doc.put("cid", item.cid);
            doc.put("age", item.age);
            doc.put("crn", item.crn);
            doc.put("vlc", item.vlc);
            doc.put("vsd", item.vsd);
            doc.put("vch", item.vch);
            doc.put("csr", item.vch);
            doc.put("pror", item.pror);
            doc.put("csor", item.csor);
            doc.put("proc", item.proc);
            doc.put("csoc", item.csoc);
            doc.put("bsid", item.bsid);
            doc.put("cpid", item.cpid);
            doc.put("tr", item.tr);
            doc.put("ui", item.ui);
            doc.put("prn", item.prn);
            doc.put("csn", item.csn);
            documents.add(doc);
        }
        database.getCollection("myCollection").insertMany(documents);
    }

    @Override
    public void finishAddData() throws Exception {
        database.getCollection("myCollection").createIndex(Document.parse("{ prg: 1 }"));
        database.getCollection("myCollection").createIndex(Document.parse("{ prr: 1 }"));
        database.getCollection("myCollection").createIndex(Document.parse("{ vlc: 1 }"));
        database.getCollection("myCollection").createIndex(Document.parse("{ vch: 1 }"));
        database.getCollection("myCollection").createIndex(Document.parse("{ tr: 1 }"));
        database.getCollection("myCollection").createIndex(Document.parse("{ bsid: 1 }"));
    }

    @Override
    public Player createPlayer() throws Exception {
        return new PlayerMongoDb();
    }

}
