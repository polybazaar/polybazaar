package ch.epfl.polybazaar.chat;

import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.database.SimpleField;

public class ChatMessage extends Model {
    public static final String ID = "id";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String LISTING_ID = "listingID";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";

    private SimpleField<String> id = new SimpleField<>(ID);
    private SimpleField<String> sender = new SimpleField<>(SENDER);
    private SimpleField<String> receiver = new SimpleField<>(RECEIVER);
    private SimpleField<String> listingID = new SimpleField<>(LISTING_ID);
    private SimpleField<String> message = new SimpleField<>(MESSAGE);
    private SimpleField<Timestamp> time = new SimpleField<>(TIME);

    public static final String COLLECTION = "chatMessages";
    public static final String OFFER_MADE = "make_offer_";
    public static final String OFFER_PROCESSED = "process_offer_";
    public static final String OFFER_ACCEPTED = "acc";
    public static final String OFFER_REFUSED = "ref";

    // no-argument constructor so that instances can be created by ModelTransaction
    public ChatMessage(){
        registerFields(id, sender, receiver, listingID, message, time);
    }

    public ChatMessage(String sender, String receiver, String listingID, String message, Timestamp time){
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

    public String getReceiver(){
        return receiver.get();
    }

    public String getListingID(){
        return listingID.get();
    }

    public String getMessage(){
        return message.get();
    }

    public Timestamp getTime(){
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

    public static Task<List<ChatMessage>> fetchConversation(String listingID) {
        return ModelTransaction.fetchFieldEquality(ChatMessage.COLLECTION, "listingID", listingID, ChatMessage.class);
    }

    public static Task<List<ChatMessage>> fetchMessagesFrom(String sender) {
        return ModelTransaction.fetchFieldEquality(ChatMessage.COLLECTION,"sender", sender, ChatMessage.class);
    }

    public static Task<List<ChatMessage>> fetchMessagesTo(String receiver) {
        return ModelTransaction.fetchFieldEquality(ChatMessage.COLLECTION,"receiver", receiver, ChatMessage.class);
    }
}
