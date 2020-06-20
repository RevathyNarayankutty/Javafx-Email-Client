package sample.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import sample.model.EmailMessage;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

public class MessageRendererService extends Service {

    private EmailMessage emailMessage;
    private WebEngine webEngine; //For displaying the emails
    private StringBuffer stringBuffer; //Holds the contents rendered by the web engine

    public MessageRendererService(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.stringBuffer= new StringBuffer();
        this.setOnSucceeded(event -> {
            displayMessage();
        });
    }

    public void setEmailMessage(EmailMessage emailMessage){
        this.emailMessage= emailMessage;
    }

    private void displayMessage(){
        webEngine.loadContent(stringBuffer.toString());
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {

                try{
                    loadMessage();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    //Loads the message inside the string buffer
    private void loadMessage() throws MessagingException, IOException {
        stringBuffer.setLength(0); //Clears the string buffer
        Message message= emailMessage.getMessage();
        String contentType= message.getContentType();
        if(isSimpleType(contentType)){
            stringBuffer.append(message.getContent().toString());
        } else if(isMultipleType(contentType)){
            Multipart multipart= (Multipart) message.getContent();
            loadMultipart(multipart, stringBuffer);
        }
    }

    private void loadMultipart(Multipart multipart, StringBuffer stringBuffer) throws MessagingException, IOException {
        for(int i= multipart.getCount()-1; i>=0; i--) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType();
            if(isSimpleType(contentType)){
                stringBuffer.append(bodyPart.getContent().toString());
            } else if(isMultipleType(contentType)){
                Multipart multipart2= (Multipart) bodyPart.getContent();
                loadMultipart(multipart2, stringBuffer);
            } else if(!isTextPlain(contentType)){
                //Here we get the attachments
                MimeBodyPart mbp= (MimeBodyPart) bodyPart;
                emailMessage.addAttachment(mbp);
            }
        }
    }

    private boolean isTextPlain(String contentType){
        return  contentType.contains("TEXT/PLAIN");
    }

    private boolean isSimpleType(String contentType){
        if(contentType.contains("TEXT/HTML")||
                contentType.contains("mixed")||
                contentType.contains("text")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isMultipleType(String contentType){
        if(contentType.contains("multipart")){
            return true;
        } else {
            return false;
        }
    }
}