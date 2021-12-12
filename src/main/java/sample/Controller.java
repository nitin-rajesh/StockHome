package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataHandler;

import java.io.IOException;

public class Controller {

    Stage stage;

    @FXML
    ComboBox<String> UserNameBox;

    @FXML
    PasswordField passwordField;

    @FXML
    void loginButton(ActionEvent e){
        DataHandler dataHandler = new DataHandler();
        dataHandler.testConnection();
    }

    @FXML
    void registerButton(ActionEvent e) throws IOException {
        String username = UserNameBox.getValue();
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
        stage.setTitle("StockHome login");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
