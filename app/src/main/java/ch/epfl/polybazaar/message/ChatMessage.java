package ch.epfl.polybazaar.message;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

import java.util.Arrays;
import java.util.Date;
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

    /**
     * Fetches the requested message
     * @param id id of the message
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static Task<ChatMessage> fetch(String id) {
        return ModelTransaction.fetch(ChatMessage.COLLECTION, id, ChatMessage.class);
    }

    /**
     * Fetches all the messages belonging to a conversation between two users for a particular listing
     * @param userEmail1 : the email of the first user
     * @param userEmail2 : the email of the second user
     * @param listingID : the ID of the listing the conversation is about
     * @return successful task containing the model instance if it exists, null otherwise. The task
     *         fails if the database is unreachable
     */

    public static Task<List<ChatMessage>> fetchMessagesOfConversation(String userEmail1, String userEmail2, String listingID) {
        return ModelTransaction.fetchAllWithMultipleFieldsEquality(ChatMessage.COLLECTION, Arrays.asList("sender", "receiver", "listingID"), Arrays.asList(userEmail1, userEmail2, listingID), ChatMessage.class);
    }
}
