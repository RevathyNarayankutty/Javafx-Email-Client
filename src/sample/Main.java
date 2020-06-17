package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.view.ViewFactory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //To display the login window
        ViewFactory viewFactory= new ViewFactory(new EmailManager());
        viewFactory.showLoginWindow();
        viewFactory.updateStyles();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
