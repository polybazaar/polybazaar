package ch.epfl.polybazaar.message;

import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.database.Field;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

public class ChatMessage extends Model {
    private Field<String> id = new Field<>("id");
    private Field<String> sender = new Field<>("sender");
    private Field<String> receiver = new Field<>("receiver");
    private Field<String> listingID = new Field<>("listingID");
    private Field<String> message = new Field<>("message");
    private Field<Date> time = new Field<>("time");

    public static final String COLLECTION = "chatMessages";

    // no-argument constructor so that instances can be created by ModelTransaction
    public ChatMessage(){
        registerFields(id, sender, receiver, listingID, message, time);
    }

    public ChatMessage(String sender, String receiver, String listingID, String message, Date time){
        this();
        this.sender.set(sender);
        this.receiver.set(receiver);
        this.listingID.set(listingID);
        this.message.set(message);
        this.time.set(time);
    }

    public String getSender(){
        return sender.get();
    }

    public String getMessage(){
        return message.get();
    }

    public Date getTime(){
        return time.get();
    }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return id.get();
    }

    @Override
    public void setId(String id) {
        this.id.set(id);
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

    public static Task<List<ChatMessage>> fetchConversation(String userEmail1, String userEmail2, String listingID) {
        return ModelTransaction.fetchMultipleFieldsEquality(ChatMessage.COLLECTION, Arrays.asList("sender", "receiver", "listingID"), Arrays.asList(userEmail1, userEmail2, listingID), ChatMessage.class);
    }
}
