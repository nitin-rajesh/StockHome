package sample.DatabaseConnection.Base;

import sample.Register;

import java.io.IOException;
import java.sql.*;

public class DataHandler <T>{
    String connectionURL = "jdbc:mysql://localhost:3306/";
    String user = "guest";
    String pwd = "neptune05";

    public DataHandler(String connectionURL){
        this.connectionURL += connectionURL;
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

    public void executeQuery(String query, T obj, dbFunction<T> function){
        try(Connection conn = DriverManager.getConnection(connectionURL,user,pwd);
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()){
                function.execute(rs,obj);
        }
        catch (SQLException | IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void executeUpdate(String update) throws SQLException {
        try(Connection conn = DriverManager.getConnection(connectionURL,user,pwd);
            Statement statement = conn.createStatement();
            ){
            statement.executeUpdate(update);
        }
    }
}
