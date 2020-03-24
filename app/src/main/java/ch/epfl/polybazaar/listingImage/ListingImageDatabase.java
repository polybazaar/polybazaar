package ch.epfl.polybazaar.listingImage;

import ch.epfl.polybazaar.database.callback.ListingImageCallback;
import ch.epfl.polybazaar.database.callback.ListingImageCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;

public abstract class ListingImageDatabase {

    public static final String listingCollectionName = "listingsImage";

    private static DataStore db;

    public static void fetchListingImage(final String listingImageID, final ListingImageCallback callback) {
        final ListingImageCallbackAdapter adapterCallback = new ListingImageCallbackAdapter(callback);
        db = DataStoreFactory.getDependency();
        db.fetchData(listingCollectionName, listingImageID, adapterCallback);
    }

    public static void storeListingImage(final ListingImage listingImage, final String listingImageID, final SuccessCallback callback){
        db = DataStoreFactory.getDependency();
        db.setData(listingCollectionName, listingImageID, listingImage, callback);
    }

    public static void deleteListing(final String listingImageID, final SuccessCallback callback){
        db = DataStoreFactory.getDependency();
        db.deleteData(listingCollectionName, listingImageID, callback);
    }
}
