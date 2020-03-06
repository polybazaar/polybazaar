package ch.epfl.polybazaar.litelisting;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.generic.GenericDatabase;

/**
 * Usage Example:
 *
 * private LiteListingDatabase lldb = new LiteListingDatabase("liteListings");
 * LiteListingCallback callbackLiteListing = new LiteListingCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(LiteListing result) {
 *                 // use result;
 *             }
 *         };
 * lldb.fetchLiteListing("myFancyLiteListing", callbackLiteListing);
 */
public class LiteListingDatabase {

    private GenericDatabase db;
    private String liteListingCollectionName;

    /**
     * @param liteListingCollectionName the Firestore collection where liteListings lie
     */
    public LiteListingDatabase(String liteListingCollectionName) {
        this.liteListingCollectionName = liteListingCollectionName;
        db = new GenericDatabase();
    }

    /**
     * set the liteListing collection name
     * @param name the name of the collection
     */
    public void setLiteListingCollectionName(String name) {
        liteListingCollectionName = name;
    }

    /**
     * Fetch all liteListings IDs from the database
     * callback will contain the liteListingList (List of IDs (Strings))
     * @param callback a LiteListingListCallback interface implementation
     */
    public void fetchLiteListingList(final LiteListingListCallback callback) {
        final LiteListingListCallbackAdapter adapterCallback= new LiteListingListCallbackAdapter(callback);
        db.getAllDataInCollection(liteListingCollectionName, adapterCallback);
    }

    /**
     * Fetch a liteListing from the database given its ID
     * callback will contain the liteListing
     * @param liteListingID the ID we give the listing
     * @param callback a LiteListingCallback interface implementation
     */
    public void fetchLiteListing(final String liteListingID, final LiteListingCallback callback){
        final LiteListingCallbackAdapter adapterCallback = new LiteListingCallbackAdapter(callback);
        db.fetchData(liteListingCollectionName, liteListingID, adapterCallback);
    }

    /**
     * Add a liteListing to the database
     * callback will contain true if successful, false otherwise
     * the liteListing's ID will we determined automatically
     * @param liteListing the liteListing
     * @param callback a SuccessCallback interface implementation
     */
    public void addLiteListing(LiteListing liteListing, final SuccessCallback callback) {
        db.addData(liteListingCollectionName, liteListing, callback);
    }

    /**
     * Deletes a liteListing from the database
     * callback will contain true if successful, false otherwise
     * @param liteListingID the liteListing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public void deleteLiteListing(String liteListingID, SuccessCallback callback) {
        db.deleteData(liteListingCollectionName, liteListingID, callback);
    }

}