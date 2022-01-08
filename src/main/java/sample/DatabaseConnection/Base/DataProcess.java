package sample.DatabaseConnection.Base;

import javafx.scene.control.ComboBox;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.PrefStack.ModeSetter;
import sample.DatabaseConnection.Records.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class DataProcess {
    static DataHandler dataHandler = new DataHandler(ModeSetter.getMode());
    public static void addUser(User user) throws SQLException {
        String query = "INSERT INTO User(Username, password, email_id) VALUES (";
        String fullquery = query.concat("'" + user.name() + "','" + user.password() + "','" + user.emailID() + "');");
        System.out.println(fullquery);
        dataHandler.executeUpdate(fullquery);
    }

    public static double getStockPrice(String symbol, String file) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"python3", file,symbol};
        Process proc = rt.exec(commands);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

// Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        double stockPrice = 0.0;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            try{
                stockPrice = Double.parseDouble(s);
            }catch (NumberFormatException ignored){}
        }
// Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }


        return stockPrice;
    }

    public static void mongoEntry(String symbol) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        String arg = "'db.recommendation.insertOne({\"symbol\":\"" +symbol+ "\"});'";
        String[] commands = {"mongo", "--eval",arg,"recommendations"};
        Process proc = rt.exec(commands);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

// Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        double stockPrice = 0.0;
        System.out.println("Mongo output");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            try{
                stockPrice = Double.parseDouble(s);
            }catch (NumberFormatException ignored){}
        }
        System.out.println("Mongo end");
// Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }
    public static void mongoRefresh() throws IOException, InterruptedException {
        String command = "mongoimport -d recommendations -c recs --type csv --file /Users/nitinrajesh/Code/StockHome/rec.csv --headerline --drop";
        Runtime rt = Runtime.getRuntime();
        String[] commands = command.split(" ");
        Process proc = rt.exec(commands);
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

// Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        double stockPrice = 0.0;
        System.out.println("Mongo output");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
            try{
                stockPrice = Double.parseDouble(s);
            }catch (NumberFormatException ignored){}
        }
        System.out.println("Mongo end");
// Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

    }

    private DataProcess(){

    }
}
