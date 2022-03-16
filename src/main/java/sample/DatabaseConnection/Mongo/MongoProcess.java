package sample.DatabaseConnection.Mongo;

import java.io.*;
import java.util.ArrayList;

public class MongoProcess {

    public static void clearDB() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nitinrajesh/Code/StockHome/rec.csv",false));
        writer.write("symbol\n");
        writer.close();
    }

    public static void writeToDB(String symbol) throws IOException {
        if(!isDuplicate(symbol)){
            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nitinrajesh/Code/StockHome/rec.csv",true));
            writer.write(symbol + "\n");
            writer.close();
        }
    }

    public static ArrayList<String> readFromDB(String dbName) throws IOException {
        ArrayList<String> arr = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("/Users/nitinrajesh/Code/StockHome/rec.csv"));
        reader.readLine();
        String line;
        while((line = reader.readLine()) != null){
            System.out.println(line);
            arr.add(line);
        }
        return arr;
    }

    private static boolean isDuplicate(String symbol) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/Users/nitinrajesh/Code/StockHome/rec.csv"));
        String line;
        while((line = reader.readLine()) != null){
            if(line.equals(symbol))
                return true;
        }
        return false;

    }
}
