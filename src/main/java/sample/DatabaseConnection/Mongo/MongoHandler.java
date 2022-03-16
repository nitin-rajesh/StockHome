package sample.DatabaseConnection.Mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MongoHandler {
    String connectionString = "mongodb://jarGuest:stockhome@Nitins-MacBook-Pro.local:27017/?maxPoolSize=20&w=majority";
    MongoClient client;
    MongoDatabase database;

    public MongoHandler(){
        client = MongoClients.create(connectionString);
        try{
            Bson command = new BsonDocument("ping",new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            System.out.println("Connected successfully");
        } catch (MongoException me){
            System.err.println("Error occurred");
        }

    }
    /*

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
    */


}
