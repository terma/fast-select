package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.demo.DemoData;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PlayerFactoryMongoDb implements PlayerFactory<DemoData> {

    private MongoClient client = new MongoClient();
    private MongoDatabase database = client.getDatabase("test");

    public PlayerFactoryMongoDb() {
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
    }

    @Override
    public Player createPlayer() throws Exception {
        return new PlayerMongoDb();
    }

}
