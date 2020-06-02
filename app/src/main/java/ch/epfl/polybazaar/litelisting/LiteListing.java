    package ch.epfl.polybazaar.litelisting;


import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.database.SimpleField;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.utilities.ImageUtilities;

/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public class LiteListing extends Model {
    public static final String LISTING_ID = "listingID";
    public static final String TITLE = "title";
    public static final String PRICE = "price";
    public static final String CATEGORY = "category";
    public static final String STRING_THUMBNAIL = "stringThumbnail";
    public static final String TIMESTAMP = "timestamp";
    public static final String TIME_SOLD = "timeSold";
    public static final String NO_THUMBNAIL = "NoThumbnail";
    public static final String THUMBNAIL_REF = "thumbnailRef";

    private final SimpleField<String> listingID = new SimpleField<>(LISTING_ID);
    private final SimpleField<String> title = new SimpleField<>(TITLE);
    private final SimpleField<String> price = new SimpleField<>(PRICE);
    private final SimpleField<String> category = new SimpleField<>(CATEGORY);
    private final SimpleField<String> stringThumbnail = new SimpleField<>(STRING_THUMBNAIL);
    private final SimpleField<Timestamp> timestamp = new SimpleField<>(TIMESTAMP);
    private final SimpleField<Timestamp> timeSold = new SimpleField<>(TIME_SOLD);
    private final SimpleField<String> thumbnailRef = new SimpleField<>(THUMBNAIL_REF);

    private Bitmap thumbnail;

    public static final String SOLD = "SOLD";
    public static final String COLLECTION = "liteListings";

    // no-argument constructor so that instances can be created by ModelTransaction
    public LiteListing() {
        registerFields(listingID, title, price, category, stringThumbnail, timestamp, timeSold, thumbnailRef);
    }

    public LiteListing(String listingID, String title, String price, String category, String stringThumbnail) {
        this();
        this.listingID.set(listingID);
        this.title.set(title);
        this.price.set(price);
        this.category.set(category);
        this.stringThumbnail.set(stringThumbnail);
        this.timestamp.set(Timestamp.now());
        this.thumbnailRef.set(NO_THUMBNAIL);
    }

    public LiteListing(String listingID, String title, String price, String category) {
        this(listingID, title, price, category, NO_THUMBNAIL);
    }

    public String getTitle() {
        return title.get();
    }

    public String getListingID() {
        return listingID.get();
    }

    public String getPrice() {
        return price.get();
    }

    public String getCategory(){
        return category.get();
    }

    public String getStringThumbnail() {
        return stringThumbnail.get();
    }

    public Task<Bitmap> fetchThumbnail(Context ctx) {
        String filename = getThumbnailRef();
        if (filename.equals(NO_THUMBNAIL)) {
            return Tasks.forResult(null);
        } else {
            return ImageTransaction.fetch(filename, ctx).onSuccessTask(bitmap -> {
                thumbnail = bitmap;
                return Tasks.forResult(bitmap);
            });
        }
    }

    public Timestamp getTimestamp() { return timestamp.get(); }

    public Timestamp getTimeSold() { return timeSold.get(); }
    
    public String getThumbnailRef() { return thumbnailRef.get(); }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return listingID.get();
    }

    @Override
    public void setId(String id) {
        this.listingID.set(id);
    }

    /**
     * Fetches the requested lite listing
     * @param id id of the listing
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static Task<LiteListing> fetch(String id) {
        return ModelTransaction.fetch(COLLECTION, id, LiteListing.class);
    }

    /**
     * Fetches all the listings in the database
     * @return task containing the a list of listings. The task
     * fails if the database is unreachable
     */
    public static Task<List<LiteListing>> fetchAll() {
        return ModelTransaction.fetchAll(COLLECTION, LiteListing.class);
    }

    public static <T> Task<Void> updateField(String field, String id, T updatedValue) {
        return ModelTransaction.updateField(COLLECTION, id, field, updatedValue);
    }

    /**
     * Updates multiple fields of a lite listing
     * @param id the lite listing's id
     * @param updated a map of fields and values to update
     * @return a void task
     */
    public static Task<Void> updateMultipleFields(String id, Map<String, Object> updated){
        return ModelTransaction.updateMultipleFields(LiteListing.COLLECTION, id, updated);
    }

    public static  Task<List<LiteListing>> fetchFieldEquality(String field, String compareValue) {
       return  ModelTransaction.fetchFieldEquality(COLLECTION, field, compareValue, LiteListing.class);
    }

    public Task<Void> deleteWithDependencies() {
        return ImageTransaction.delete(getThumbnailRef())
                .onSuccessTask(aVoid -> delete());
    }
}
