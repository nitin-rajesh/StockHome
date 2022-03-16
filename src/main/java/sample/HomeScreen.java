package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.Mongo.MongoHandler;
import sample.DatabaseConnection.Mongo.MongoProcess;
import sample.DatabaseConnection.PrefStack.ModeSetter;
import sample.DatabaseConnection.PrefStack.PrefReader;
import sample.DatabaseConnection.PrefStack.PrefSettings;
import sample.DatabaseConnection.PrefStack.PrefWriter;
import sample.DatabaseConnection.Records.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeScreen implements Initializable {

    User user;

    ArrayList<Text> textRepo = new ArrayList<>();
    ArrayList<Button> buttonRepo = new ArrayList<>();
    ArrayList<Integer> transactionIDs = new ArrayList<>();

    MongoHandler mongoHandler;

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
    private DataHandler<String> stockHome;

    @FXML
    void searchStock(ActionEvent event){
        long startTime = System.nanoTime();

        focus = FocusModes.SEARCH;
        Integer search = 0;
        System.out.println("Here");
        String searchKey = searchBar.getText();
        String query = "Select * from Company where Trade_name='" + searchKey + "' or Name like '%" + searchKey + "%';";
        DataHandler<Integer> dataHandler = new DataHandler<>(ModeSetter.getMode());
        frontView.getItems().clear();
        dataHandler.executeQuery(query, search,(rc, count)->{
            while(rc.next() && count < 80){
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
                    long sTime = System.nanoTime();

                    try {
                        String searchWord;
                        if(ModeSetter.getMode().equals("StockHome")){
                            searchWord = coSymbol.getText();
                        }
                        else{
                            searchWord = coName.getText().replace(" ","-").toLowerCase(Locale.ROOT);
                        }
                        double marketPrice = DataProcess.getStockPrice(searchWord, ModeSetter.getMode().concat("_scraper.py"));
                        coPrice.setText(new DecimalFormat("#.##").format(marketPrice));

                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                    }

                    long eTime = System.nanoTime();
                    double ttsort = ((eTime - sTime)/1000000.0);
                    System.out.println("Price thread: " + ttsort + "ms");
                }).start();
                buyButton.setOnAction(actionEvent -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Transaction alert");
                    alert.setContentText("Are you sure you want to proceed?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == ButtonType.OK) {


                        String searchWord;
                        if(ModeSetter.getMode().equals("StockHome")){
                            searchWord = coSymbol.getText();
                        }
                        else{
                            searchWord = coName.getText().replace(" ","-").toLowerCase(Locale.ROOT);
                        }
                        String q = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                                + "(" + Double.parseDouble(coPrice.getText()) + ", " + Integer.parseInt(quantity.getText())
                                + ", '" + searchWord+ "', '" + user.emailID() + "', 'b');";
                        System.out.println(q);
                        try {
                            new DataHandler<String>(ModeSetter.getMode()).executeUpdate(q);
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
        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Show recommendations: " + timeToSort + "µs");

    }

    @FXML
    void showRecommendations(ActionEvent e) throws IOException, InterruptedException {
        long startTime = System.nanoTime();


        ArrayList<String> symbols = MongoProcess.readFromDB("recommendation");
        DataProcess.mongoRefresh();
        System.out.println(symbols.stream().toArray().toString());
        Integer search = 0;
        frontView.getItems().clear();
        for(String sym: symbols){
        String query = "Select * from Company where Trade_name='" + sym +"';";
        DataHandler<Integer> dataHandler = new DataHandler<>(ModeSetter.getMode());

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
                        String searchWord;
                        if(ModeSetter.getMode().equals("StockHome")){
                            searchWord = coSymbol.getText();
                        }
                        else{
                            searchWord = coName.getText().replace(" ","-").toLowerCase(Locale.ROOT);
                        }
                        double marketPrice = DataProcess.getStockPrice(searchWord, ModeSetter.getMode().concat("_scraper.py"));
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

                        String searchWord;
                        if(ModeSetter.getMode().equals("StockHome")){
                            searchWord = coSymbol.getText();
                        }
                        else{
                            searchWord = coName.getText().replace(" ","-").toLowerCase(Locale.ROOT);
                        }
                        String q = "INSERT INTO TRANSACTION(Price,Quantity,Txn_TradeName,Txn_UserID,Buy_or_sell) VALUES"
                                + "(" + Double.parseDouble(coPrice.getText()) + ", " + Integer.parseInt(quantity.getText())
                                + ", '" + searchWord + "', '" + user.emailID() + "', 'b');";
                        System.out.println(q);
                        try {
                            new DataHandler<String>(ModeSetter.getMode()).executeUpdate(q);
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
        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Show recommendations: " + timeToSort + "µs");

    }

    @FXML
    void showTransactions(ActionEvent e){
        long startTime = System.nanoTime();

        focus = FocusModes.TRANSACTION;
        DataHandler<String> dataHandler = new DataHandler<>(ModeSetter.getMode());
        String query = "SELECT * FROM Transaction WHERE Txn_userID = '" + user.emailID() + "';";
        frontView.getItems().clear();
        try{
            MongoProcess.clearDB();
        }catch (IOException ignored){}

        dataHandler.executeQuery(query, query, (rc, ignored)->{
            while(rc.next()){
                AtomicBoolean toSell = new AtomicBoolean(false);
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

                        double marketPrice = DataProcess.getStockPrice(coName.getText(), ModeSetter.getMode().concat("_scraper.py"));
                        updatedPrice.setText("Market price: " + new DecimalFormat(".##").format(marketPrice));
                        double roiVal = 100*(marketPrice - Double.parseDouble(coPrice.getText()))/Double.parseDouble(coPrice.getText());
                        String opString = new DecimalFormat("#.##").format(roiVal) + "%";
                        if(roiVal > 0){
                            //DataProcess.mongoEntry(coName.getText());
                            MongoProcess.writeToDB(coName.getText());
                            opString = "+" + opString;
                            ROI.setFill(Color.GREEN);
                            toSell.set(true);
                        }
                        else{
                            opString = "-" + opString;
                            ROI.setFill(Color.RED);
                            toSell.set(false);
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
                        new DataHandler<String>(ModeSetter.getMode()).executeUpdate(sellQuery);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    String dropQuery = "DELETE FROM TRANSACTION WHERE Transaction_ID = " + tID + ";";
                    System.out.println(dropQuery);
                    try {
                        new DataHandler<String>(ModeSetter.getMode()).executeUpdate(dropQuery);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    showTransactions(e);
                });

                if(rc.getString("Buy_or_sell").equals("b")){
                    temp.getChildren().add(sellButton);
                    //mongoHandler.addRec(rc.getString("Txn_TradeName"),user.emailID(),ROI.getText(), toSell.get());
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

        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Show transactions: " + timeToSort + "µs");

    }

    @FXML
    void showCompanies(ActionEvent e){

    }

    @FXML
    void showGroups(ActionEvent e){
        long startTime = System.nanoTime();

        focus = FocusModes.GROUP;
        DataHandler<String> dataHandler = new DataHandler<>(ModeSetter.getMode());
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
                new DataHandler<String>(ModeSetter.getMode()).executeQuery(q,null,(grpRC,grpIgnore) -> {
                    while (grpRC.next()){
                        String qc = "SELECT * FROM Company WHERE Trade_name = '" + grpRC.getString("Item_TradeName") +"';";
                        System.out.println(qc);
                        new DataHandler<String>(ModeSetter.getMode()).executeQuery(qc,null,(cmpRC,cmpIgnored) -> {
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
                                        String searchWord;
                                        if(ModeSetter.getMode().equals("StockHome")){
                                            searchWord = coSymbol.getText();
                                        }
                                        else{
                                            searchWord = coName.getText().replace(" ","-").toLowerCase(Locale.ROOT);
                                        }
                                        double marketPrice = DataProcess.getStockPrice(searchWord, ModeSetter.getMode().concat("_scraper.py"));
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

        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Show groups: " + timeToSort + "µs");


    }

    @FXML
    void addGroup(ActionEvent e) throws IOException {
        long startTime = System.nanoTime();

        Stage popStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/dialogue_box.fxml"));
        popStage.setTitle("Create group");
        popStage.setScene(new Scene(root));
        popStage.show();

        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Add group: " + timeToSort + "µs");

    }
    @FXML
    void addCompanyToGroup(ActionEvent e) throws SQLException, IOException {
        long startTime = System.nanoTime();

        if(focus != FocusModes.GROUP){
            HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
            Text obj = (Text)selection.getChildren().get(0);
            String query = "INSERT INTO GroupItem (Item_grpID,Item_tradeName) VALUES (1,'"
                    + obj.getText() + "');";
            System.out.println(query);
            new DataHandler<String>(ModeSetter.getMode()).executeUpdate(query);
            Stage popStage = new Stage();

            Parent root = FXMLLoader.load(getClass().getResource("/dialogue_box.fxml"));
            popStage.setTitle("Select group");
            popStage.setScene(new Scene(root));
            popStage.show();
        }

        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Add company: " + timeToSort + "µs");

    }

    @FXML
    void dropCompanyFromGroup(ActionEvent e) throws SQLException {
        long startTime = System.nanoTime();
        HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
        if(selection.getChildren().size() > 1){
            Text grpName = (Text) selection.getChildren().get(0);
            String query = "DELETE from GroupItem WHERE Item_TradeName = '" + grpName.getText() +"';";
            new DataHandler<>(ModeSetter.getMode()).executeUpdate(query);
            showGroups(e);
        }
        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Drop company: " + timeToSort + "µs");
    }

    @FXML
    void dropGroup(ActionEvent e) throws SQLException {
        long startTime = System.nanoTime();

        HBox selection = frontView.getSelectionModel().getSelectedItems().get(0);
        if(selection.getChildren().size() == 1){
            Text grpName = (Text) selection.getChildren().get(0);
            String query = "DELETE from StockGroup WHERE Group_name = '" + grpName.getText() +"';";
            new DataHandler<>(ModeSetter.getMode()).executeUpdate(query);
            showGroups(e);
        }

        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Drop group: " + timeToSort + "µs");

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        stage.setTitle("Login");
        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED,(KeyEvent event)->{
            //System.out.println(event.getText());
            if(event.isAltDown() && event.isShiftDown()){
                if(event.getCode().toString().equals("ENTER")){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Forbidden treasure unlocked");
                    alert.setContentText("Do you want to switch modes?");
                    Image image = new Image(Objects.requireNonNull(getClass().getResource("/f5c.jpg")).toExternalForm());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(80);
                    imageView.setFitWidth(65);
                    alert.setGraphic(imageView);
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == ButtonType.OK)
                        ModeSetter.switchMode();
                    Controller temp = loader.getController();
                    temp.headingText.setText(ModeSetter.getMode());
                    temp.refreshUsernames();
                    ModeSetter.getMode();
                }
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        long startTime = System.nanoTime();
        PrefSettings p = new PrefReader();
        user = new User(p.readValues().get(1),p.readValues().get(0),p.readValues().get(2));
        heading.setText("Hi, " + user.name());
        //System.out.println(user.toString());
        showTransactions(new ActionEvent());
        focus = FocusModes.TRANSACTION;
        long endTime = System.nanoTime();
        double timeToSort = ((endTime - startTime)/1000.0);
        System.out.println("Startup time: " + timeToSort + "µs");

    }
}
