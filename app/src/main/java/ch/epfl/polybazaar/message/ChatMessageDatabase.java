package ch.epfl.polybazaar.message;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.database.ModelTransaction;

public class ChatMessageDatabase {

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


           /* for (ChatMessage chatMessage : chatMessages) {

                if (chatMessage.getListingID().equals(listingID) && ((chatMessage.getSender().equals(userEmail1) && chatMessage.getReceiver().equals(userEmail2))
                                                                    || (chatMessage.getSender().equals(userEmail2) && chatMessage.getReceiver().equals(userEmail1)))) {
                    conversationMessages.add(chatMessage);
                }
            }*/