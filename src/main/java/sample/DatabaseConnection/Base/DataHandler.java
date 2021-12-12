package sample.DatabaseConnection.Base;

import sample.Register;

import java.sql.*;

public class DataHandler {
    String connectionURL = "jdbc:mysql://localhost:3306/StockHome";
    String user = "root";
    String pwd = "neptune05";

    public DataHandler(){
        //ctor
    }
    DataHandler(String connectionURL){
        this.connectionURL = connectionURL;
    }

    public void testConnection() {
        try(Connection conn = DriverManager.getConnection(connectionURL,user,pwd);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Company where Trade_Name = 'AAPL';");
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                String name = rs.getString("Name");
                double marketCap = rs.getDouble("Market_cap");
                System.out.println("Name: " + name + "|Market_cap: " + marketCap);

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    ResultSet executeQuery(String query){
        try(Connection conn = DriverManager.getConnection(connectionURL,user,pwd);
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()){
            return rs;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
