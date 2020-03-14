package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.util.Objects;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;

public  class ListingCallbackAdapter implements DocumentSnapshotCallback {

    private ListingCallback listingCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param listingCallback the ListingCallback to be adapted
     */
    public ListingCallbackAdapter(ListingCallback listingCallback){
        this.listingCallback = listingCallback;
    }

    @Override
    public void onCallback(DocumentSnapshot result) {
        if (result==null){
            listingCallback.onCallback(null);
            return;
        }
        Listing listing = new Listing(Objects.requireNonNull(result.get("title")).toString(),
                Objects.requireNonNull(result.get("description")).toString(),
                Objects.requireNonNull(result.get("price")).toString(),
                Objects.requireNonNull(result.get("userEmail")).toString(),
                (File) result.get("image"));
        listingCallback.onCallback(listing);
    }
}