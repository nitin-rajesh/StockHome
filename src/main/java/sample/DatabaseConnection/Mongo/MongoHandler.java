package sample.DatabaseConnection.Mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.Records.User;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MongoHandler {
    MongoClient client = new MongoClient("192.168.0.118",27017);
    MongoDatabase database;
    MongoCollection<Document> buyableCollection;
    MongoCollection<Document> sellableCollection;

    public MongoHandler(){
        database = client.getDatabase("recommendations");
    }

    public void addRec(String companySymbol, String email, String change, boolean toSell) throws IOException, InterruptedException {
        System.out.println("In addRec()");
        MongoCollection<Document> collection = database.getCollection(email);
        Document recDoc = new Document(companySymbol,"MongoDB")
               .append("symbol",companySymbol)
               //.append("price", DataProcess.getStockPrice(companySymbol))
                .append("change",change);
       if(toSell){
           recDoc.append("status","sell");
       }
       else{
           recDoc.append("status","keep");
       }
       collection.insertOne(recDoc);
    }

    public List<Document> getRecs(User user){
        List<Document> documentList = new ArrayList<>();
        MongoCollection<Document> mongoCollection = database.getCollection(user.emailID());
        FindIterable<Document> iterableDoc = mongoCollection.find();
        Iterator it = iterableDoc.iterator();
        while(it.hasNext()){
            while ((it.hasNext())){
                documentList.add((Document) it.next());
                System.out.println(documentList.get(documentList.size() - 1));
            }
        }
        return documentList;
    }

    public void clearRecs(User user){
        database.getCollection(user.emailID()).drop();
        System.out.println("Table dropped");
    }



}
