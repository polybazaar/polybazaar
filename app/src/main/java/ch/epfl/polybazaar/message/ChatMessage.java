package ch.epfl.polybazaar.message;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

public class ChatMessage extends Model {
    @DocumentId
    private String id;
    private String sender;
    private String receiver;
    private String listingID;
    private String message;

    public static final String COLLECTION = "messages";

    public ChatMessage(){};

    public ChatMessage(String sender, String receiver, String listingID, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.listingID = listingID;
        this.message = message;
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
