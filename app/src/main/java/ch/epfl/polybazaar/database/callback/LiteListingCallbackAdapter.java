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

        Object image = result.get("stringThumbnail");
        String stringThumbnail = "";
        if (image != null) {
            stringThumbnail = image.toString();
        }
        LiteListing liteListing = new LiteListing(String.valueOf(result.get("listingID")),
                String.valueOf(result.get("title")),
                String.valueOf(result.get("price")),
                String.valueOf(result.get("category")),
                stringThumbnail);
        liteListingCallback.onCallback(liteListing);
    }
}