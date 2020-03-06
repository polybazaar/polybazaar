package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.database.generic.GenericCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

public  class LiteListingCallbackAdapter implements GenericCallback {

    private LiteListingCallback liteListingCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param liteListingCallback the LiteListingCallback to be adapted
     */
    public LiteListingCallbackAdapter(LiteListingCallback liteListingCallback){
        this.liteListingCallback = liteListingCallback;
    }

    @Override
    public void onCallback(DocumentSnapshot result) {
        if (result==null){
            liteListingCallback.onCallback(null);
            return;
        }
        LiteListing liteListing = new LiteListing(result.get("listingID").toString(),
                result.get("title").toString(),
                result.get("price").toString());
        liteListingCallback.onCallback(liteListing);
    }
}