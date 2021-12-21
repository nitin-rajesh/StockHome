package sample;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataProcess;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("StockHome login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        //Creating a Mongo client
        MongoClient mongo = MongoClients.create( "mongodb://192.168.0.118:27017" );
        //Connecting to the database
        MongoDatabase database = mongo.getDatabase("test");
        //Creating multiple collections
        database.createCollection("sampleCollection1");
        database.createCollection("sampleCollection2");
        database.createCollection("sampleCollection3");
        database.createCollection("sampleCollection4");
        //Retrieving the list of collections
        MongoIterable<String> list = database.listCollectionNames();
        System.out.println("List of collections:");
        for (String name : list) {
            System.out.println(name);
        }
        database.getCollection("sampleCollection4").drop();
        System.out.println("Collection dropped successfully");
        System.out.println("List of collections after the delete operation:");
        for (String name : list) {
            System.out.println(name);
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
