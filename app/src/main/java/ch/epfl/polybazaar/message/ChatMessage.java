package ch.epfl.polybazaar.message;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

import ch.epfl.polybazaar.database.Model;

public class ChatMessage extends Model {
    @DocumentId
    private String id;
    private String sender;
    private String receiver;
    private String listingID;
    private String message;
    private Date time;

    public static final String COLLECTION = "chatMessages";

    public ChatMessage(){};

    public ChatMessage(String sender, String receiver, String listingID, String message, Date time){
        this.sender = sender;
        this.receiver = receiver;
        this.listingID = listingID;
        this.message = message;
        this.time = time;
    }

    public String getSender(){
        return sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public String getListingID(){
        return listingID;
    }

    public String getMessage(){
        return message;
    }

    public Date getTime(){
        return time;
    }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
