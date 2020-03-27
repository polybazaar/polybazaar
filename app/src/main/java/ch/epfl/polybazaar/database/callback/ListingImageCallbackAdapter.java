package ch.epfl.polybazaar.database.callback;

import android.os.Build;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.listingImage.ListingImage;

public class ListingImageCallbackAdapter implements DataSnapshotCallback {

    private ListingImageCallback listingImageCallback;

    public ListingImageCallbackAdapter(ListingImageCallback listingImageCallback){
        this.listingImageCallback = listingImageCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            listingImageCallback.onCallback(null);
            return;
        }
        ListingImage listingImage = new ListingImage(String.valueOf(result.get("image")),
                String.valueOf(result.get("refNextImg")));

        listingImageCallback.onCallback(listingImage);
    }
}
