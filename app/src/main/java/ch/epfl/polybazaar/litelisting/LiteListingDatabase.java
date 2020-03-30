package ch.epfl.polybazaar.litelisting;

import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.callback.StringListCallbackAdapter;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;
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

    public static final String liteListingCollectionName = "liteListings";

    private static DataStore db;

    /**
     * Fetch all liteListings IDs from the database
     * callback will contain the liteListingList (List of IDs (Strings))
     * @param callback a LiteListingListCallback interface implementation
     */

    public static void fetchLiteListingList(final StringListCallback callback) {
        final StringListCallbackAdapter adapterCallback= new StringListCallbackAdapter(callback);
        db = DataStoreFactory.getDependency();
        db.getAllDataInCollection(liteListingCollectionName, adapterCallback);
    }


    /**
     * Fetch a liteListing from the database given its ID
     * callback will contain the liteListing
     * @param liteListingID the ID we give the listing
     * @param callback a LiteListingCallback interface implementation
     */
    public static void fetchLiteListing(final String liteListingID, final LiteListingCallback callback){
        final LiteListingCallbackAdapter adapterCallback = new LiteListingCallbackAdapter(callback);
        db = DataStoreFactory.getDependency();
        db.fetch(liteListingCollectionName, liteListingID, adapterCallback);
    }

    /**
     * Add a liteListing to the database
     * callback will contain true if successful, false otherwise
     * the liteListing's ID will we determined automatically
     * @param liteListing the liteListing
     * @param callback a SuccessCallback interface implementation
     */
    public static void addLiteListing(final LiteListing liteListing, final SuccessCallback callback) {
        db = DataStoreFactory.getDependency();
        db.addData(liteListingCollectionName, liteListing, callback);
    }

    /**
     * Deletes a liteListing from the database
     * callback will contain true if successful, false otherwise
     * @param liteListingID the liteListing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteLiteListing(final String liteListingID, final SuccessCallback callback) {
        db = DataStoreFactory.getDependency();
        db.deleteData(liteListingCollectionName, liteListingID, callback);
    }

    /**
     * Performs a query which returns all liteListing IDs where their field == equalTo
     * @param field the field to be checked for equality
     * @param equalTo what field should be equal to
     * @param callback a StringListCallback interface implementation
     */
    public static void queryLiteListingStringEquality(final String field, final String equalTo, final StringListCallback callback) {
        db = DataStoreFactory.getDependency();
        db.queryStringEquality(liteListingCollectionName, field, equalTo, callback);
    }

}