package sample.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.EmailManager;
import sample.controller.*;

import java.io.IOException;
import java.util.ArrayList;

public class ViewFactory {

    private EmailManager emailManager;

    //For getting the active stages.
    //Initialisation done in the ViewFactory constructor.
    //Stages assigned in initializeStage() and removed in closeStage()
    private ArrayList<Stage> activeStages;

    private boolean mainViewInitialized= false;

    //This constructor will pass the View events to the Email manager which will do all the work.
    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        activeStages= new ArrayList<Stage>();
    }

    public boolean isMainViewInitialized(){
        return  mainViewInitialized;
    }

    //View options handling:
    @FXML
    private  ColorTheme colorTheme= ColorTheme.DEFAULT;

    @FXML
    private FontSize fontSize= FontSize.MEDIUM;

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    //To generate a login stage:
    public void showLoginWindow(){
        //The following code is to get the fxml controller to each .fxml files without using the inbuilt code.
        //Gets the login page (LoginWindow.fxml will be called)
        //This approach explicitly gets the fx:controller="path"
        BaseController controller= new LoginWindowController(emailManager, this, "LoginWindow.fxml");
        initializeStage(controller);
    }

    //To interact between multiple stages:
    public void showMainWindow(){
        BaseController controller= new MainWindowController(emailManager, this, "MainWindow.fxml");
        initializeStage(controller);
        mainViewInitialized= true;
    }

    public void showOptionWindow(){
        BaseController controller= new OptionWindowController(emailManager, this, "OptionWindow.fxml");
        initializeStage(controller);
    }

    public void showComposeMessageWindow(){
        BaseController controller= new ComposeMessageController(emailManager, this, "ComposeMessageWindow.fxml");
        initializeStage(controller);
    }

    public void showEmailDetailsWindow(){
        BaseController controller= new EmailDetailsController(emailManager, this, "EmailDetailWindow.fxml");
        initializeStage(controller);
    }

    //To extract the common implementations.
    //The following code is common for any scenes that we create
    private void initializeStage(BaseController baseController){
        FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
        fxmlLoader.setController(baseController);
        Parent parent;

        //Using try- catch as load() always causes errors.
        try {
            parent=fxmlLoader.load(); //load() will read the .fxml window from the disk.
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
        Scene scene= new Scene(parent);
        Stage stage= new Stage();
        stage.setScene(scene);
        stage.show();
        activeStages.add(stage);
    }

    //To close the windows that are not in use.
    //Eg: After login, we should only have the main window. Thus, we need to close the login window.
    public void closeStage(Stage stageToClose){
        stageToClose.close();
        activeStages.remove(stageToClose);
    }

    @FXML
    public void updateStyles() {
        for(Stage stage: activeStages){
            Scene scene= stage.getScene();

            //Handling the CSS
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
            scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());
        }
    }
}
