package ch.epfl.polybazaar.litelisting;


import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.database.SimpleField;

/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public class LiteListing extends Model {
    private final SimpleField<String> listingID = new SimpleField<>("listingID");
    private final SimpleField<String> title = new SimpleField<>("title");
    private final SimpleField<String> price = new SimpleField<>("price");
    private final SimpleField<String> category = new SimpleField<>("category");
    private final SimpleField<String> stringThumbnail = new SimpleField<>("stringThumbnail");
    private final SimpleField<Timestamp> timestamp = new SimpleField<>("timestamp");

    public static final String COLLECTION = "liteListings";

    // no-argument constructor so that instances can be created by ModelTransaction
    public LiteListing() {
        registerFields(listingID, title, price, category, stringThumbnail, timestamp);
    }

    public LiteListing(String listingID, String title, String price, String category, String stringThumbnail) {
        this();
        this.listingID.set(listingID);
        this.title.set(title);
        this.price.set(price);
        this.category.set(category);
        this.stringThumbnail.set(stringThumbnail);
        this.timestamp.set(Timestamp.now());
    }

    public LiteListing(String listingID, String title, String price, String category) {
        this(listingID, title, price, category, "");
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

    public Timestamp getTimestamp() { return timestamp.get(); }

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
}
