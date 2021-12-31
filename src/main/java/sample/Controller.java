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
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.PrefStack.PrefSettings;
import sample.DatabaseConnection.PrefStack.PrefWriter;
import sample.DatabaseConnection.Records.User;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Controller implements Initializable {

    Stage stage;

    @FXML
    ComboBox<String> userNameBox;

    @FXML
    PasswordField passwordField;

    @FXML
    Text infoText;

    @FXML
    void loginButton(ActionEvent e) throws SQLException {

        DataHandler<ActionEvent> dataHandler = new DataHandler<>();
        String query = "SELECT * FROM User WHERE email_ID = '" + userNameBox.getValue() + "';";
        dataHandler.executeQuery(query,e,(resultSet, actionEvent)->{
            while(resultSet.next()){
                if(passwordField.getText().equals(resultSet.getString("Password"))){
                    stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                    User user = new User(resultSet.getString("Email_ID"),resultSet.getString("Username"),resultSet.getString("Password"));
                    PrefSettings p = new PrefWriter();
                    p.writeValues(user.name(),user.emailID(),user.password());
                    try{
                        Parent root = FXMLLoader.load(getClass().getResource("/home_screen.fxml"));
                        stage.setTitle("StockHome - " + resultSet.getString("Username"));
                        stage.setScene(new Scene(root));
                        stage.show();
                    }catch (IOException ignored){}
                }
                else{
                    infoText.setText("Check username or password");
                }
            }

        });
    }

    @FXML
    void registerButton(ActionEvent e) throws IOException {
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
