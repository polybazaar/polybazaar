package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.database.generic.GenericCallback;

public  class ListingCallbackAdapter implements GenericCallback {

    private ListingCallback listingCallback;

    /**
     * adapt a callback receiving a listing to a callback receiving a DocumentSnapshot
     * @param listingCallback callback to be adapted
     */
    public ListingCallbackAdapter(ListingCallback listingCallback){
        this.listingCallback = listingCallback;
    }
    @Override
    public void onCallback(DocumentSnapshot result) {
        Listing listing = new Listing(result.get("title").toString(),
                result.get("description").toString(),result.get("price").toString());
        listingCallback.onCallback(listing);
    }
}