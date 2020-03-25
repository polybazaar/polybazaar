package ch.epfl.polybazaar.database.callback;

import android.os.Build;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            liteListingCallback.onCallback(null);
            return;
        }
        LiteListing liteListing = new LiteListing(String.valueOf(result.get("listingID")),
                String.valueOf(result.get("title")),
                String.valueOf(result.get("price")),
                String.valueOf(result.get("category")));
                String.valueOf(result.get("thumbnail"));
        liteListingCallback.onCallback(liteListing);
    }
}