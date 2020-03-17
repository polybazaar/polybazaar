package ch.epfl.polybazaar.database.callback;

import android.os.Build;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            listingCallback.onCallback(null);
            return;
        }
        Listing listing = new Listing(String.valueOf(result.get("title")),
                String.valueOf(result.get("description")),
                String.valueOf(result.get("price")),
                String.valueOf(result.get("userEmail")));

        listingCallback.onCallback(listing);
    }
}