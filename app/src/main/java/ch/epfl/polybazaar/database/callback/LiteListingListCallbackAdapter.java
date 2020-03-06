package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.generic.GenericCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

public class LiteListingListCallbackAdapter  implements GenericCallback {

    private LiteListingListCallback liteListingListCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param liteListingListCallback the LiteListingListCallback to be adapted
     */
    public LiteListingListCallbackAdapter(LiteListingListCallback liteListingListCallback){
        this.liteListingListCallback = liteListingListCallback;
    }

    @Override
    public void onCallback(DocumentSnapshot result) {
        if (result==null){
            liteListingListCallback.onCallback(null);
            return;
        }
        Map<String, Object> listMap = result.getData();
        List<String> liteListingList = new ArrayList<>();
        for (String ID : listMap.keySet()) {
            liteListingList.add(ID);
        }
        liteListingListCallback.onCallback(liteListingList);
    }
}
