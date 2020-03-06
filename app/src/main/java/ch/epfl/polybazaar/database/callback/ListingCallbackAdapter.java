package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.database.generic.GenericCallback;

public  class ListingCallbackAdapter implements GenericCallback {

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
        Listing listing = new Listing(result.get("title").toString(),
                result.get("description").toString(),
                result.get("price").toString(),
                result.get("userEmail").toString());
        listingCallback.onCallback(listing);
    }
}