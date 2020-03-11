package ch.epfl.polybazaar.listing;

import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;

import static ch.epfl.polybazaar.database.generic.GenericDatabase.*;

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

    private static final String listingCollectionName = "listings";

    /**
     * Fetch a listing from the database
     * callback will contain the listing
     * @param listingID the ID we give the listing
     * @param callback a ListingCallback interface implementation
     */
    public static void fetchListing(final String listingID, final ListingCallback callback){
        final ListingCallbackAdapter adapterCallback = new ListingCallbackAdapter(callback);
        fetchData(listingCollectionName, listingID, adapterCallback);
    }

    /**
     * Add a listing to the database
     * callback will contain true if successful, false otherwise
     * @param listing the listing
     * @param listingID the ID the listing should get
     * @param callback a SuccessCallback interface implementation
     */
    public static void storeListing(final Listing listing, final String listingID, final SuccessCallback callback){
        setData(listingCollectionName, listingID, listing, callback);
    }

    /**
     * Deletes a listing from the database
     * callback will contain true if successful, false otherwise
     * @param listingID the listing's ID
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteListing(final String listingID, final SuccessCallback callback){
        deleteData(listingCollectionName, listingID, callback);
    }

}