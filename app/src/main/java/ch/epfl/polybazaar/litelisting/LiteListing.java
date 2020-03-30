package ch.epfl.polybazaar.litelisting;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public class LiteListing extends Model {
    @DocumentId
    private String id;
    private String listingID;
    private String title;
    private String price;
    private String category;
    private String stringThumbnail;

    public static final String COLLECTION = "liteListings";

    public LiteListing() {}

    public LiteListing(String listingID, String title, String price, String category, String stringThumbnail) {
        this.listingID = listingID;
        this.title = title;
        this.price = price;
        this.category = category;
        this.stringThumbnail = stringThumbnail;
    }

    public LiteListing(String listingID, String title, String price, String category) {
        this(listingID, title, price, category, "");
    }

    public String getTitle() {
        return title;
    }

    public String getListingID() {
        return listingID;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory(){
        return category;
    }

    public String getStringThumbnail() {
        return stringThumbnail;
    }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public static Task<List<LiteListing>> retrieveAll() {
        return ModelTransaction.fetchAll(COLLECTION, LiteListing.class);
    }
}
