package ch.epfl.polybazaar.database.callback;

import java.util.Objects;

import ch.epfl.polybazaar.database.DataSnapshot;
import ch.epfl.polybazaar.database.DataSnapshotCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

public class LiteListingCallbackAdapter implements DataSnapshotCallback {

    private LiteListingCallback liteListingCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param liteListingCallback the LiteListingCallback to be adapted
     */
    public LiteListingCallbackAdapter(LiteListingCallback liteListingCallback){
        this.liteListingCallback = liteListingCallback;
    }

    @Override
    public void onCallback(DataSnapshot result) {
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