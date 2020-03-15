package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;
import ch.epfl.polybazaar.listing.Listing;

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
        Object image = result.get("stringImage");
        String stringImage = null;
        if (image != null) {
            stringImage = image.toString();
        }
        Listing listing = new Listing(Objects.requireNonNull(result.get("title")).toString(),
                Objects.requireNonNull(result.get("description")).toString(),
                Objects.requireNonNull(result.get("price")).toString(),
                Objects.requireNonNull(result.get("userEmail")).toString(),
                stringImage);
        listingCallback.onCallback(listing);
    }
}