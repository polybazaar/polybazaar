package ch.epfl.polybazaar.testingUtilities;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.user.User;

import static java.util.UUID.randomUUID;

public abstract  class DatabaseStoreUtilities {

    public static void storeNewListing(String title, String userEmail, String id){

        Listing testListing = new Listing(title,"testDescription","22",userEmail, "Multimedia");
        LiteListing testLiteListing = new LiteListing(id, title, "22", "Multimedia");
        testListing.setId(id);
        testLiteListing.setId(id);

        Tasks.whenAll(testListing.save(), testLiteListing.save()).addOnFailureListener((v) -> {
            throw new AssertionError();
        });
    }

    public static void storeNewListing(String title, String userEmail){
        final String newListingID = randomUUID().toString();
        storeNewListing(title, userEmail, newListingID);
    }

    public static void storeNewMessage(String sender, String receiver, String listingID, String message){
        ChatMessage chatMessage = new ChatMessage(sender, receiver, listingID, message, new Timestamp(new Date(System.currentTimeMillis())));
        Tasks.whenAll(chatMessage.save()).addOnFailureListener(e -> {
            throw new AssertionError();
        });
    }

    public static void storeNewUser(String nickname, String email) {
        User user = new User(nickname, email);
        try {
            Tasks.await(user.save());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
