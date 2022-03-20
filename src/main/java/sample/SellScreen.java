package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.PrefStack.ModeSetter;
import sample.DatabaseConnection.PrefStack.PrefReader;
import sample.DatabaseConnection.PrefStack.PrefSettings;
import sample.DatabaseConnection.Records.User;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SellScreen implements Initializable {

    @FXML
    Text titleText;

    @FXML
    Text stocksOwned;

    @FXML
    Text stockPrice;

    @FXML
    Text netPrice;

    @FXML
    TextField stocksToSell;

    @FXML
    Text tID;

    private boolean isSellable = false;

    User user;

    @FXML
    void sellStocks(ActionEvent e){
        if(isSellable) {
            String sellQuery = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                    + "(" + Double.parseDouble(stockPrice.getText()) + ", " + Integer.parseInt(stocksToSell.getText())
                    + ", '" + titleText.getText()+"', '" + user.emailID() + "', 's');";
            System.out.println(sellQuery);

            String leftoverQuery = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                    + "(" + Double.parseDouble(stockPrice.getText()) + ", "
                    + (Integer.parseInt(stocksOwned.getText().substring(1)) - Integer.parseInt(stocksToSell.getText()))
                    + ", '" + titleText.getText()+"', '" + user.emailID() + "', 'b');";
            System.out.println(sellQuery);
            try {
                new DataHandler<String>(ModeSetter.getMode()).executeUpdate(sellQuery);
                new DataHandler<String>(ModeSetter.getMode()).executeUpdate(leftoverQuery);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String dropQuery = "DELETE FROM TRANSACTION WHERE Transaction_ID = " + tID.getText() + ";";
            System.out.println(dropQuery);
            try {
                new DataHandler<String>(ModeSetter.getMode()).executeUpdate(dropQuery);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancel(ActionEvent e){
        System.out.println("Cancel");
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread priceVal = new Thread(()->{
            while (true)
                calculateNetPrice();
        });
        priceVal.setDaemon(true);
        priceVal.start();
        PrefSettings p = new PrefReader();
        user = new User(p.readValues().get(1),p.readValues().get(0),p.readValues().get(2));
        System.out.println("Init");
    }

    void calculateNetPrice(){

        try{
           int stockQuantity = Integer.parseInt(stocksToSell.getText());
           int totalStocks = Integer.parseInt(stocksOwned.getText().substring(1));
            if(stockQuantity <= totalStocks){
                isSellable = true;
                Double netPriceVal = Double.parseDouble(stockPrice.getText())* stockQuantity;
                netPrice.setText(new DecimalFormat("#.##").format(netPriceVal));
            }
            else{
                isSellable = false;
                netPrice.setText("Out of bounds");
            }


        } catch (NumberFormatException ignored) {
            netPrice.setText("0.00");
            isSellable = false;
        }
    }
}
