package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.PrefStack.PrefReader;
import sample.DatabaseConnection.PrefStack.PrefSettings;
import sample.DatabaseConnection.PrefStack.PrefWriter;
import sample.DatabaseConnection.Records.User;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeScreen implements Initializable {

    User user;

    ArrayList<Text> textRepo = new ArrayList<>();
    ArrayList<Button> buttonRepo = new ArrayList<>();
    ArrayList<Integer> transactionIDs = new ArrayList<>();

    Stage stage;

    @FXML
    Text heading;

    @FXML
    MenuBar menuBar;

    @FXML
    ListView<HBox> frontView;

    @FXML
    TextField searchBar;

    FocusModes focus;

    @FXML
    void searchStock(ActionEvent event){
        focus = FocusModes.SEARCH;
        Integer search = 0;
        System.out.println("Here");
        String searchKey = searchBar.getText();
        String query = "Select * from Company where Trade_name='" + searchKey + "' or Name like '%" + searchKey + "%';";
        DataHandler<Integer> dataHandler = new DataHandler<>();
        frontView.getItems().clear();
        dataHandler.executeQuery(query, search,(rc, count)->{
            while(rc.next() && count < 20){
                count++;
                HBox temp = new HBox();
                temp.setSpacing(20);
                Text coName = new Text(rc.getString("Name"));
                System.out.println(coName.getText());
                Text coSymbol = new Text(rc.getString("Trade_name"));
                Text coPrice = new Text("...");
                TextField quantity = new TextField();
                Button buyButton = new Button("Buy");
                textRepo.add(coName);
                textRepo.add(coPrice);
                textRepo.add(coSymbol);
                buttonRepo.add(buyButton);
                new Thread(()->{
                    try {
                        double marketPrice = DataProcess.getStockPrice(coSymbol.getText());
                        coPrice.setText(new DecimalFormat("#.##").format(marketPrice));

                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();
                buyButton.setOnAction(actionEvent -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Transaction alert");
                    alert.setContentText("Are you sure you want to proceed?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == ButtonType.OK) {
                        String q = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                                + "(" + Double.parseDouble(coPrice.getText()) + ", " + Integer.parseInt(quantity.getText())
                                + ", '" + coSymbol.getText() + "', '" + user.emailID() + "', 'b');";
                        System.out.println(q);
                        try {
                            new DataHandler<String>().executeUpdate(q);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Transaction");
                        alert.setContentText("Transaction complete");
                        alert.show();
                    }
                });
                temp.getChildren().addAll(coSymbol,coName,coPrice,quantity,buyButton);
                frontView.getItems().add(0,temp);
            }
        });

    }

    @FXML
    void showTransactions(ActionEvent e){
        focus = FocusModes.TRANSACTION;
        DataHandler<String> dataHandler = new DataHandler<>();
        String query = "SELECT * FROM Transaction WHERE Txn_userID = '" + user.emailID() + "';";
        frontView.getItems().clear();
        dataHandler.executeQuery(query, query, (rc, ignored)->{
            while(rc.next()){
                HBox temp = new HBox();
                Integer tID = rc.getInt("Transaction_ID");
                Text coPrice = new Text(new DecimalFormat("#.##").format(rc.getDouble("Price")));
                Text coQuantity = new Text("x"+rc.getString("Quantity"));
                Text currentPrice = new Text("Net: " + rc.getDouble("Price")*rc.getDouble("Quantity"));
                Text updatedPrice = new Text("...");
                Text ROI = new Text(" ");
                Text coName = new Text(rc.getString("Txn_TradeName"));
                Button sellButton = new Button("Sell");
                temp.getChildren().addAll(coName,coPrice,coQuantity,currentPrice, updatedPrice, ROI);
                textRepo.add(coPrice);
                textRepo.add(coName);
                textRepo.add(coQuantity);
                textRepo.add(updatedPrice);
                textRepo.add(ROI);
                textRepo.add(coName);
                buttonRepo.add(sellButton);
                transactionIDs.add(tID);
                new Thread(()->{
                    try {
                        double marketPrice = DataProcess.getStockPrice(coName.getText());
                        updatedPrice.setText("Market price: " + new DecimalFormat(".##").format(marketPrice));
                        double roiVal = 100*(marketPrice - Double.parseDouble(coPrice.getText()))/Double.parseDouble(coPrice.getText());
                        String opString = new DecimalFormat("#.##").format(roiVal) + "%";
                        if(roiVal > 0){
                            opString = "+" + opString;
                            ROI.setFill(Color.GREEN);
                        }
                        else{
                            opString = "-" + opString;
                            ROI.setFill(Color.RED);
                        }
                        ROI.setText(opString);

                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                    }
                }).start();

                sellButton.setOnAction(actionEvent -> {
                    String sellQuery = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                            + "(" + Double.parseDouble(updatedPrice.getText().substring("Market price: ".length())) + ", " + Integer.parseInt(coQuantity.getText().substring(1))
                            + ", '" + coName.getText()+"', '" + user.emailID() + "', 's');";
                    System.out.println(sellQuery);
                    try {
                        new DataHandler<String>().executeUpdate(sellQuery);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    String dropQuery = "DELETE FROM TRANSACTION WHERE Transaction_ID = " + tID + ";";
                    System.out.println(dropQuery);
                    try {
                        new DataHandler<String>().executeUpdate(dropQuery);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    showTransactions(e);
                });

                if(rc.getString("Buy_or_sell").equals("b")){
                    temp.getChildren().add(sellButton);
                }
                else{
                    Text soldText = new Text("Sold");
                    soldText.setFont(Font.font("System", FontPosture.ITALIC,13));
                    temp.getChildren().add(soldText);
                }
                temp.setSpacing(20);
                frontView.getItems().add(0,temp);
            }
        });
    }

    @FXML
    void showCompanies(ActionEvent e){

    }

    @FXML
    void showGroups(ActionEvent e){
        focus = FocusModes.GROUP;
        DataHandler<String> dataHandler = new DataHandler<>();
        String query = "SELECT * FROM StockGroup WHERE Group_userID = '" + user.emailID() + "';";
        frontView.getItems().clear();
        dataHandler.executeQuery(query, query, (rc, ignored)->{
            while(rc.next()){
                HBox temp = new HBox();
                ToolBar grpBar = new ToolBar();
                Text groupName = new Text(rc.getString("Group_name"));
                groupName.setFont(Font.font("DIN Alternate",20));
                //grpBar.getItems().add(groupName);
                temp.getChildren().add(groupName);
                String q = "SELECT * FROM GroupItem WHERE Item_grpID = " + rc.getString("Group_ID") +";";
                new DataHandler<String>().executeQuery(q,null,(grpRC,grpIgnore) -> {
                    while (grpRC.next()){
                        String qc = "SELECT * FROM Company WHERE Trade_name = '" + grpRC.getString("Item_TradeName") +"';";
                        System.out.println(qc);
                        new DataHandler<String>().executeQuery(qc,null,(cmpRC,cmpIgnored) -> {
                            int count = 0;
                            while(cmpRC.next() && count < 20){
                                count++;
                                HBox temp1 = new HBox();
                                temp1.setSpacing(20);
                                Text coName = new Text(cmpRC.getString("Name"));
                                System.out.println(coName.getText());
                                Text coSymbol = new Text(cmpRC.getString("Trade_name"));
                                Text coPrice = new Text("...");
                                TextField quantity = new TextField();
                                Button buyButton = new Button("Buy");
                                textRepo.add(coName);
                                textRepo.add(coPrice);
                                textRepo.add(coSymbol);
                                buttonRepo.add(buyButton);
                                new Thread(()->{
                                    try {
                                        double marketPrice = DataProcess.getStockPrice(coSymbol.getText());
                                        coPrice.setText(new DecimalFormat("#.##").format(marketPrice));

                                    } catch (IOException | InterruptedException ioException) {
                                        ioException.printStackTrace();
                                    }
                                }).start();
                                temp1.getChildren().addAll(coSymbol,coName,coPrice);
                                frontView.getItems().add(0,temp1);
                            }
                        });
                    }
                });

                temp.setSpacing(20);
                frontView.getItems().add(0,temp);
            }
        });


    }

    @FXML
    void addGroup(ActionEvent e) throws IOException {
        Stage popStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/dialogue_box.fxml"));
        popStage.setTitle("Create group");
        popStage.setScene(new Scene(root));
        popStage.show();
    }
    @FXML
    void addCompanyToGroup(ActionEvent e) throws SQLException, IOException {
        if(focus != FocusModes.GROUP){
            HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
            Text obj = (Text)selection.getChildren().get(0);
            String query = "INSERT INTO GroupItem (Item_grpID,Item_tradeName) VALUES (1,'"
                    + obj.getText() + "');";
            System.out.println(query);
            new DataHandler<String>().executeUpdate(query);
            Stage popStage = new Stage();

            Parent root = FXMLLoader.load(getClass().getResource("/dialogue_box.fxml"));
            popStage.setTitle("Select group");
            popStage.setScene(new Scene(root));
            popStage.show();
        }
    }

    @FXML
    void dropCompanyFromGroup(ActionEvent e) throws SQLException {
        HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
        if(selection.getChildren().size() > 1){
            Text grpName = (Text) selection.getChildren().get(0);
            String query = "DELETE from GroupItem WHERE Item_TradeName = '" + grpName.getText() +"';";
            new DataHandler<>().executeUpdate(query);
            showGroups(e);
        }
    }

    @FXML
    void dropGroup(ActionEvent e) throws SQLException {
        HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
        if(selection.getChildren().size() == 1){
            Text grpName = (Text) selection.getChildren().get(0);
            String query = "DELETE from StockGroup WHERE Group_name = '" + grpName.getText() +"';";
            new DataHandler<>().executeUpdate(query);
            showGroups(e);
        }
    }

    @FXML
    void accountDetails(ActionEvent e){

    }

    @FXML
    void logOut(ActionEvent e) throws IOException {
        textRepo.clear();
        buttonRepo.clear();
        transactionIDs.clear();
        new PrefWriter().writeValues(" "," "," ");
        stage = (Stage) (menuBar).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setTitle("StockHome login");
        stage.setScene(new Scene(root));
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PrefSettings p = new PrefReader();
        user = new User(p.readValues().get(1),p.readValues().get(0),p.readValues().get(2));
        heading.setText("Hi, " + user.name());
        //System.out.println(user.toString());
        showTransactions(new ActionEvent());
        focus = FocusModes.TRANSACTION;
    }
}
