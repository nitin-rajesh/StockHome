package sample.DatabaseConnection.Base;

import sample.DatabaseConnection.Records.Company;
import sample.DatabaseConnection.Records.Recommendation;
import sample.DatabaseConnection.Records.StockGroup;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface dbFunction <T>{
    public void execute(ResultSet rs, T obj) throws SQLException, IOException, InterruptedException;
}
