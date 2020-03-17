package ch.epfl.polybazaar.database.callback;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class LiteListingListCallbackAdapter implements CollectionSnapshotCallback {

    private LiteListingListCallback liteListingListCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param liteListingListCallback the LiteListingListCallback to be adapted
     */

    public LiteListingListCallbackAdapter(LiteListingListCallback liteListingListCallback){
        this.liteListingListCallback = liteListingListCallback;
    }

    @Override
    public void onCallback(CollectionSnapshot result) {
        if (result==null){
            liteListingListCallback.onCallback(null);
            return;
        }
        List<String> liteListingList = new ArrayList<>();
        for (DataSnapshot doc : result.getDocuments()) {
            liteListingList.add(doc.getId());
        }
        liteListingListCallback.onCallback(liteListingList);
    }
}
