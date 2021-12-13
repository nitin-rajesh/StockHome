package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.Base.DataProcess;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    Stage stage;

    @FXML
    ComboBox<String> userNameBox;

    @FXML
    PasswordField passwordField;

    @FXML
    void loginButton(ActionEvent e) throws SQLException {
        try{
            System.out.println(DataProcess.getStockPrice("GOOGL"));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        /*DataHandler dataHandler = new DataHandler();
        dataHandler.testConnection();
        dataHandler.executeQuery("SELECT * FROM Company WHERE Market_cap > 1000000000000.00",(resultSet)->{
            while(resultSet.next())
            System.out.println(resultSet.getString("Name"));
        });*/
    }

    @FXML
    void registerButton(ActionEvent e) throws IOException {
        String username = userNameBox.getValue();
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
        stage.setTitle("Create new account");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DataHandler<ComboBox<String>> dataHandler = new DataHandler<>();
        String query = "SELECT Email_ID from User";
        dataHandler.executeQuery(query, userNameBox, (rc,userNameBox)->{
            ArrayList<String> emailIDs = new ArrayList<>();
            while(rc.next()){
                emailIDs.add(rc.getString("Email_ID"));
            }
            for(String email: emailIDs) {
                userNameBox.getItems().add(email);
            }
        });
    }
}
