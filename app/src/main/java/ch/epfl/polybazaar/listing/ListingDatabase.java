package ch.epfl.polybazaar.listing;

import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * Usage Example:
 *
 * ListingCallback callbackListing = new ListingCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(Listing result) {
 *                 // use result;
 *             }
 *         };
 * fetchListing("myFancyListing", callbackListing);
 */
public abstract class ListingDatabase {

    public static final String listingCollectionName = "listings";

    private static DataStore db;

    /**
     * Fetch a listing from the database
     * callback will contain the listing
     * @param listingID the ID we give the listing
     * @param callback a ListingCallback interface implementation
     */
    public static void fetchListing(final String listingID, final ListingCallback callback){
        final ListingCallbackAdapter adapterCallback = new ListingCallbackAdapter(callback);
        db = DataStoreFactory.getDependency();
        db.fetch(listingCollectionName, listingID, adapterCallback);
    }

    /**
     * Add a listing to the database
     * callback will contain true if successful, false otherwise
     * @param listing the listing
     * @param listingID the ID the listing should get
     * @param callback a SuccessCallback interface implementation
     */
    public static void storeListing(final Listing listing, final String listingID, final SuccessCallback callback){
        db = DataStoreFactory.getDependency();
        db.set(listingCollectionName, listingID, listing, callback);
    }

    /**
     * Deletes a listing from the database
     * callback will contain true if successful, false otherwise
     * @param listingID the listing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteListing(final String listingID, final SuccessCallback callback){
        db = DataStoreFactory.getDependency();
        db.delete(listingCollectionName, listingID, callback);
    }

    /**
     * Performs a query which returns all listing IDs where their field == equalTo
     * @param field the field to be checked for equality
     * @param equalTo what field should be equal to
     * @param callback a StringListCallback interface implementation
     */
    public static void queryListingStringEquality(final String field, final String equalTo, final StringListCallback callback) {
        db = DataStoreFactory.getDependency();
        db.queryStringEquality(listingCollectionName, field, equalTo, callback);
    }

}