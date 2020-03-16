package ch.epfl.polybazaar.litelisting;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.database.Datastore;
import ch.epfl.polybazaar.database.DatastoreFactory;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;


/**
 * Usage Example:
 *
 * LiteListingCallback callbackLiteListing = new LiteListingCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(LiteListing result) {
 *                 // use result;
 *             }
 *         };
 * fetchLiteListing("myFancyLiteListing", callbackLiteListing);
 */
public abstract class LiteListingDatabase {

    private static final String liteListingCollectionName = "liteListings";
    private static Datastore db;
    /**
     * Fetch all liteListings IDs from the database
     * callback will contain the liteListingList (List of IDs (Strings))
     * @param callback a LiteListingListCallback interface implementation
     */
    public static void fetchLiteListingList(final LiteListingListCallback callback) {
        final LiteListingListCallbackAdapter adapterCallback= new LiteListingListCallbackAdapter(callback);
        db = DatastoreFactory.getDependency();
        //getAllDataInCollection(liteListingCollectionName, adapterCallback);
    }

    /**
     * Fetch a liteListing from the database given its ID
     * callback will contain the liteListing
     * @param liteListingID the ID we give the listing
     * @param callback a LiteListingCallback interface implementation
     */
    public static void fetchLiteListing(final String liteListingID, final LiteListingCallback callback){
        final LiteListingCallbackAdapter adapterCallback = new LiteListingCallbackAdapter(callback);
        db = DatastoreFactory.getDependency();
        db.fetchData(liteListingCollectionName, liteListingID, adapterCallback);
    }

    /**
     * Add a liteListing to the database
     * callback will contain true if successful, false otherwise
     * the liteListing's ID will we determined automatically
     * @param liteListing the liteListing
     * @param callback a SuccessCallback interface implementation
     */
    public static void addLiteListing(final LiteListing liteListing, final SuccessCallback callback) {
        db = DatastoreFactory.getDependency();
        Map<String,Object> data = new HashMap<>();
        data.put("listingID",liteListing.getListingID());
        data.put("title",liteListing.getTitle());
        data.put("price",liteListing.getPrice());
        db.addData(liteListingCollectionName, data, callback);
    }

    /**
     * Deletes a liteListing from the database
     * callback will contain true if successful, false otherwise
     * @param liteListingID the liteListing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteLiteListing(final String liteListingID, final SuccessCallback callback) {
        db = DatastoreFactory.getDependency();
        db.deleteData(liteListingCollectionName, liteListingID, callback);
    }

}