package ch.epfl.polybazaar.database.callback;

import java.util.Objects;

import ch.epfl.polybazaar.database.DataSnapshot;
import ch.epfl.polybazaar.database.DataSnapshotCallback;
import ch.epfl.polybazaar.listing.Listing;

public  class ListingCallbackAdapter implements DataSnapshotCallback {

    private ListingCallback listingCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param listingCallback the ListingCallback to be adapted
     */
    public ListingCallbackAdapter(ListingCallback listingCallback){
        this.listingCallback = listingCallback;
    }

    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            listingCallback.onCallback(null);
            return;
        }
       /* Listing listing = new Listing(Objects.requireNonNull(result.get("title")).toString(),
                Objects.requireNonNull(result.get("description")).toString(),
                Objects.requireNonNull(result.get("price")).toString(),
                Objects.requireNonNull(result.get("userEmail")).toString());

        */
        Listing listing = (Listing)result.data();
        listingCallback.onCallback(listing);
    }
}