package sample.controller;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import sample.EmailManager;
import sample.controller.services.MessageRendererService;
import sample.model.EmailMessage;
import sample.model.EmailTreeItem;
import sample.model.SizeInteger;
import sample.view.ViewFactory;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    public TreeView<String> emailsTreeView; //For Email folders

    @FXML
    public TableView<EmailMessage> emailsTableView; //For Email messages

    @FXML
    private TableColumn<EmailMessage, String> senderCol;

    @FXML
    private TableColumn<EmailMessage, String> subjectCol;

    @FXML
    private TableColumn<EmailMessage, String> recipientCol;

    @FXML
    private TableColumn<EmailMessage, SizeInteger> sizeCol;

    @FXML
    private TableColumn<EmailMessage, Date> dateCol;

    @FXML
    public WebView emailsWebView; //For reading emails

    private MessageRendererService messageRendererService;

    private MenuItem markUnreadMenuItem= new MenuItem("Mark as unread");
    private MenuItem deleteMessageMenuItem= new MenuItem("Delete message");

    public MainWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void addAccountAction(){
        viewFactory.showLoginWindow();
    }

    @FXML
    void composeMessageAction(){
        viewFactory.showComposeMessageWindow();
    }

    @FXML
    void optionsAction(){
        System.out.println("Options clicked");
        viewFactory.showOptionWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupEmailsTreeView();
        setupEmailsTableView();
        setupFolderSelection();
        setupBoldRows();
        setupMessageRendererService();
        setupMessageSelection();
        setupContextMenu();
    }

    private void setupContextMenu() {
        markUnreadMenuItem.setOnAction(event -> {
            emailManager.setUnread();
        });

        deleteMessageMenuItem.setOnAction(event -> {
            emailManager.deleteSelectedMessage();
            emailsWebView.getEngine().loadContent("");
        });
    }

    //The setupMessageRendererService() must be called each time we click on an email
    private void setupMessageSelection() {
        emailsTableView.setOnMouseClicked(event -> {
            EmailMessage emailMessage= emailsTableView.getSelectionModel().getSelectedItem();
            if(emailMessage!= null){
                emailManager.setSelectedMessage(emailMessage);
                if(!emailMessage.isRead()){
                    emailManager.setRead();
                }
                emailManager.setSelectedMessage(emailMessage);
                messageRendererService.setEmailMessage(emailMessage);
                messageRendererService.restart();
            }
        });
    }

    private void setupMessageRendererService() {

        messageRendererService= new MessageRendererService(emailsWebView.getEngine());
    }

    private void setupBoldRows() {
        emailsTableView.setRowFactory(new Callback<TableView<EmailMessage>, TableRow<EmailMessage>>() {
            @Override
            public TableRow<EmailMessage> call(TableView<EmailMessage> param) {
                return new TableRow<EmailMessage>(){
                    @Override
                    protected  void updateItem(EmailMessage item, boolean empty){
                        super.updateItem(item, empty);
                        if (item!= null){
                            if(item.isRead()){
                                setStyle(" ");
                            } else {
                                setStyle("-fx-font-weight: bold");
                            }
                        }
                    }
                };
            }
        });
    }

    private void setupFolderSelection() {
        emailsTreeView.setOnMouseClicked(e->{
            EmailTreeItem<String> item= (EmailTreeItem<String>)emailsTreeView.getSelectionModel().getSelectedItem();
            if(item!= null){
                emailManager.setSelectedFolder(item);
                emailsTableView.setItems(item.getEmailMessages());
            }
        });
    }

    private void setupEmailsTableView() {
        senderCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("sender")));
        subjectCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("subject")));
        recipientCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("recipient")));
        sizeCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, SizeInteger>("size")));
        dateCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, Date>("date")));

        emailsTableView.setContextMenu(new ContextMenu(markUnreadMenuItem, deleteMessageMenuItem));

    }

    private void setupEmailsTreeView() {
        emailsTreeView.setRoot(emailManager.getFoldersRoot());
        emailsTreeView.setShowRoot(false);
    }

}