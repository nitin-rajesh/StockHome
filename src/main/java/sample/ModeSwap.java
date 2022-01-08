package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.DatabaseConnection.PrefStack.ModeSetter;

import java.net.URL;
import java.util.ResourceBundle;

public class ModeSwap implements Initializable {

    @FXML
    Text infoText;

    @FXML
    public void switchButton(ActionEvent e){
        ModeSetter.switchMode();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelButton(ActionEvent e){
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String text = "Do you want to switch from Mini Project to Self Study?";
        if(ModeSetter.getMode().equals("CryptoBase")){
            text = "Do you want to switch from Self Study to Mini Project?";
        }
        infoText.setText(text);
    }
}
