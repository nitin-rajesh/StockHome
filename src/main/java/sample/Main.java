package sample;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import javafx.application.Application;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.DatabaseConnection.Base.DataProcess;
import sample.DatabaseConnection.PrefStack.ModeSetter;

import java.util.Objects;
import java.util.Optional;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login");
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
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
