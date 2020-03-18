package ch.epfl.polybazaar.database.callback;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class StringListCallbackAdapter implements CollectionSnapshotCallback {

    private StringListCallback stringListCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param stringListCallback the LiteListingListCallback to be adapted
     */

    public StringListCallbackAdapter(StringListCallback stringListCallback){
        this.stringListCallback = stringListCallback;
    }

    @Override
    public void onCallback(CollectionSnapshot result) {
        if (result==null){
            stringListCallback.onCallback(null);
            return;
        }
        List<String> liteListingList = new ArrayList<>();
        for (DataSnapshot doc : result.getDocuments()) {
            liteListingList.add(doc.getId());
        }
        stringListCallback.onCallback(liteListingList);
    }
}
