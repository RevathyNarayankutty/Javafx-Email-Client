//This class will point to EmailManager.java and ViewFactory.java
//Used to control the communication between classes.
//Shows use of inheritance in abstract classes

package sample.controller;

import sample.EmailManager;
import sample.view.ViewFactory;

public abstract class BaseController {

    protected EmailManager emailManager;
    protected ViewFactory viewFactory;
    public String fxmlName; //Indication to the FXML files


    public BaseController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    public String getFxmlName(){
        return fxmlName;
    }
}
