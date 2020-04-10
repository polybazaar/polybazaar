package ch.epfl.polybazaar.message;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

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

    /**
     * Fetches the requested message
     * @param id id of the message
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static Task<ChatMessage> fetch(String id) {
        return ModelTransaction.fetch(COLLECTION, id, ChatMessage.class);
    }

}
