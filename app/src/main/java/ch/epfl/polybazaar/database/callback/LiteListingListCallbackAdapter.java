package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.database.generic.QuerySnapshotCallback;

public class LiteListingListCallbackAdapter implements QuerySnapshotCallback {

    private LiteListingListCallback liteListingListCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param liteListingListCallback the LiteListingListCallback to be adapted
     */
    public LiteListingListCallbackAdapter(LiteListingListCallback liteListingListCallback){
        this.liteListingListCallback = liteListingListCallback;
    }

    @Override
    public void onCallback(QuerySnapshot result) {
        if (result==null){
            liteListingListCallback.onCallback(null);
            return;
        }
        List<String> liteListingList = new ArrayList<>();
        for (DocumentSnapshot doc : result.getDocuments()) {
            liteListingList.add(doc.getId());
        }
        liteListingListCallback.onCallback(liteListingList);
    }
}
