package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.PrefStack.PrefReader;
import sample.DatabaseConnection.Records.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DialogueBox implements Initializable {

    @FXML
    ComboBox<String> groupNames;

    @FXML
    void okayButton(ActionEvent e) throws SQLException {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        PrefReader p = new PrefReader();
        User user = new User(p.readValues().get(1),p.readValues().get(0),p.readValues().get(2));
        String query1 = "SELECT * FROM STOCKGROUP WHERE Group_name = '" + groupNames.getValue() +"';";
        String gpName = groupNames.getValue();
        new DataHandler<String>().executeQuery(query1,gpName,(rc1,gname)-> {
            if(!rc1.next()){
                String query = "INSERT INTO StockGroup (Group_name,Group_userID) VALUES ('" + gname + "','" + user.emailID() + "');";
                new DataHandler<String>().executeUpdate(query);
            }

            String query = "select Group_ID from StockGroup where Group_name = '" + groupNames.getValue() + "';";
            new DataHandler<String>().executeQuery(query, null, (rc, ignore) -> {
                while (rc.next()) {
                    String q = "update GroupItem set Item_grpID = " + rc.getString("group_id") + " where item_grpID = 1";
                    new DataHandler<String>().executeUpdate(q);
                }
            });

        });
        stage.close();
    }

    @FXML
    void cancelButton(ActionEvent e){
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DataHandler<ComboBox<String>> dataHandler = new DataHandler<>();
        String query = "SELECT Group_name from StockGroup where group_id != 1;";
        dataHandler.executeQuery(query, groupNames, (rc,userNameBox)->{
            ArrayList<String> emailIDs = new ArrayList<>();
            while(rc.next()){
                emailIDs.add(rc.getString("Group_name"));
            }
            for(String email: emailIDs) {
                userNameBox.getItems().add(email);
            }
        });
    }
}
