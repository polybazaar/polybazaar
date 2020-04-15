package ch.epfl.polybazaar.testingUtilities;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Date;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.message.ChatMessage;

import static java.util.UUID.randomUUID;

public abstract  class DatabaseStoreUtilities {

    public static void storeNewListing(String title, String userEmail, String id){

        Listing testListing = new Listing(title,"testDescription","22",userEmail, "Games");
        LiteListing testLiteListing = new LiteListing(id, title, "22", "Games");
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
        ChatMessage chatMessage = new ChatMessage(sender, receiver, listingID, message, new Date(System.currentTimeMillis()));
        Tasks.whenAll(chatMessage.save()).addOnFailureListener(e -> {
            throw new AssertionError();
        });
    }
}
