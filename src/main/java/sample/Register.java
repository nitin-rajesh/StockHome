package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.Records.User;

import java.io.IOException;
import java.sql.SQLException;

public class Register {
    Stage stage;

    @FXML
    TextField userName;

    @FXML
    TextField emailID;

    @FXML
    PasswordField passwordField;

    @FXML
    PasswordField confirmPassword;

    @FXML
    Text infoText;

    @FXML
    void createAccount(ActionEvent e) throws SQLException, IOException {
        User user = new User(emailID.getText(), userName.getText(), passwordField.getText());
        if(!passwordField.getText().equals(confirmPassword.getText()))
            infoText.setText("Passwords not matching");
        else
            DataProcess.addUser(user);
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/home_screen.fxml"));
        stage.setTitle("StockHome - " + user.name());
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void goBack(ActionEvent e) throws IOException {
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        stage.setTitle("StockHome login");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
