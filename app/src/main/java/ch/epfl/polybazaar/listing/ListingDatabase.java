package ch.epfl.polybazaar.listing;

import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.generic.GenericDatabase;

/**
 * Usage Example:
 *
 * private ListingDatabase ldb = new ListingDatabase("listings");
 * ListingCallback callbackListing = new ListingCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(Listing result) {
 *                 // use result;
 *             }
 *         };
 * ldb.fetchListing("myFancyListing", callbackListing);
 */
public class ListingDatabase {

    private GenericDatabase db;
    private  String listingCollectionName;

    /**
     * @param listingCollectionName the Firestore collection where listings lie
     */
    public ListingDatabase(String listingCollectionName){
        this.listingCollectionName = listingCollectionName;
        db = new GenericDatabase();
    }

    /**
     * set the listing collection name
     * @param name the name of the collection
     */
    public void setListingCollectionName(String name){
        listingCollectionName = name;
    }

    /**
     * Fetch a listing from the database
     * callback will contain the listing
     * @param listingID the ID we give the listing
     * @param callback a callback interface implementation
     */
    public void fetchListing(final String listingID, final ListingCallback callback){
        final ListingCallbackAdapter adapterCallback = new ListingCallbackAdapter(callback);
        db.fetchData(listingCollectionName, listingID, adapterCallback);

    }

    /**
     * Add a listing to the database
     * callback will contain true if successful, false otherwise
     * @param listing the listing
     * @param callback a callback interface implementation
     */
    public  void storeListing(Listing listing, final SuccessCallback callback){
        db.setData(listingCollectionName,listing.getTitle(), listing, callback);
    }

    /**
     * Deletes a listing from the database
     * callback will contain true if successful, false otherwise
     * @param listingID the listing's ID
     * @param callback a callback interface implementation
     */
    public  void deleteListing(String listingID, SuccessCallback callback){
        db.deleteData(listingCollectionName,listingID, callback);
    }

}