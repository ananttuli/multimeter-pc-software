package prototype;


import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.Point2D;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import prototype.utils.SerialComm;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/mainscene.fxml"));
        window.setTitle("Multimeter");
        window.setScene(new Scene(root));
        window.setResizable(false);
        window.setMaximized(false);
        window.getIcons().add(new Image("/multimeter_icon.png"));
        window.show();

    }

    @Override
    public void stop() throws Exception {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
