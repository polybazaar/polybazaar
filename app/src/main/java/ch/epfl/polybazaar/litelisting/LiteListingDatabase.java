package ch.epfl.polybazaar.litelisting;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;

import static ch.epfl.polybazaar.database.generic.GenericDatabase.*;

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

    /**
     * Fetch all liteListings IDs from the database
     * callback will contain the liteListingList (List of IDs (Strings))
     * @param callback a LiteListingListCallback interface implementation
     */
    public static void fetchLiteListingList(final LiteListingListCallback callback) {
        final LiteListingListCallbackAdapter adapterCallback= new LiteListingListCallbackAdapter(callback);
        getAllDataInCollection(liteListingCollectionName, adapterCallback);
    }

    /**
     * Fetch a liteListing from the database given its ID
     * callback will contain the liteListing
     * @param liteListingID the ID we give the listing
     * @param callback a LiteListingCallback interface implementation
     */
    public static void fetchLiteListing(final String liteListingID, final LiteListingCallback callback){
        final LiteListingCallbackAdapter adapterCallback = new LiteListingCallbackAdapter(callback);
        fetchData(liteListingCollectionName, liteListingID, adapterCallback);
    }

    /**
     * Add a liteListing to the database
     * callback will contain true if successful, false otherwise
     * the liteListing's ID will we determined automatically
     * @param liteListing the liteListing
     * @param callback a SuccessCallback interface implementation
     */
    public static void addLiteListing(final LiteListing liteListing, final SuccessCallback callback) {
        addData(liteListingCollectionName, liteListing, callback);
    }

    /**
     * Deletes a liteListing from the database
     * callback will contain true if successful, false otherwise
     * @param liteListingID the liteListing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteLiteListing(final String liteListingID, final SuccessCallback callback) {
        deleteData(liteListingCollectionName, liteListingID, callback);
    }

}