package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sample.EmailManager;
import sample.controller.services.LoginService;
import sample.model.EmailAccount;
import sample.view.ViewFactory;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController extends BaseController implements Initializable {

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;


    public LoginWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void clickButton() {
        if(fieldsAreValid()){
            EmailAccount emailAccount= new EmailAccount(emailAddressField.getText(), passwordField.getText());
            LoginService loginService= new LoginService(emailAccount, emailManager); //Starting a service
            loginService.start(); //Will perform all background tasks
            //This will be only called if the TASK state is SUCCEEDED
            loginService.setOnSucceeded(event -> {

                EmailLoginResult emailLoginResult= loginService.getValue();

                switch (emailLoginResult){
                    case SUCCESS:
                        System.out.println("Login Successful " +emailAccount);

                        //JavaFX doesn't have a built-in method to get the present stage.
                        //(Stage)- casting
                        Stage stage= (Stage) errorLabel.getScene().getWindow();
                        viewFactory.closeStage(stage);
                        if(!viewFactory.isMainViewInitialized()){
                            viewFactory.showMainWindow();
                        }
                        return;
                    case FAILED_BY_CREDENTIALS:
                        System.out.println("Invalid Credentials");
                        return;
                    case FAILED_BY_UNEXPECTED_ERROR:
                        System.out.println("Unexpected error");
                        return;
                    default:
                        return;
                }
            });
        }
    }

    private boolean fieldsAreValid() {
        if (emailAddressField.getText().isEmpty()){
            errorLabel.setText("Please enter the username/ email");
            return false;
        }

        if (passwordField.getText().isEmpty()){
            errorLabel.setText("Please enter the password");
            return false;
        }

        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAddressField.setText("javafxtutorial20@gmail.com");
        passwordField.setText("shared12345");
    }
}

