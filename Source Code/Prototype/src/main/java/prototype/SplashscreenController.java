package prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by anant on 07-Mar-17.
 */
public class SplashscreenController {
    @FXML
    Button continueButton;

    public void setSceneFromReference(Node referenceNode, String nextSceneFile){

        Stage currentWindow;

        currentWindow = (Stage)referenceNode.getScene().getWindow();
        try {
            currentWindow.setScene(new Scene(FXMLLoader.load(getClass().getResource(nextSceneFile))));
            currentWindow.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onClickContinueButton(){
        setSceneFromReference(continueButton,"/mainscene.fxml");

    }
}
