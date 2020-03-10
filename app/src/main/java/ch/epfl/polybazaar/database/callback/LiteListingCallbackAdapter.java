package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

public  class LiteListingCallbackAdapter implements DocumentSnapshotCallback {

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
        LiteListing liteListing = new LiteListing(Objects.requireNonNull(result.get("listingID")).toString(),
                Objects.requireNonNull(result.get("title")).toString(),
                Objects.requireNonNull(result.get("price")).toString());
        liteListingCallback.onCallback(liteListing);
    }
}