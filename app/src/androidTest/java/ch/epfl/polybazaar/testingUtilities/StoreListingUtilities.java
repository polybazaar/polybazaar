package ch.epfl.polybazaar.testingUtilities;

import com.google.android.gms.tasks.Tasks;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static java.util.UUID.randomUUID;

public abstract  class StoreListingUtilities {

    public static void storeNewListing(String title, String userEmail){
        final String newListingID = randomUUID().toString();
        Listing testListing = new Listing(title,"testDescription","22",userEmail, "Games");
        LiteListing testLiteListing = new LiteListing(newListingID, title, "22", "Games");
        testListing.setId(newListingID);
        testLiteListing.setId(newListingID);

        Tasks.whenAll(testListing.save(), testLiteListing.save()).addOnFailureListener((v) -> {
            throw new AssertionError();
        });
    }
}
