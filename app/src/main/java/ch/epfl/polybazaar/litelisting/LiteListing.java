package ch.epfl.polybazaar.litelisting;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.database.Field;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public class LiteListing extends Model {
    @DocumentId
    private final Field<String> listingID = new Field<>("listingID");
    private final Field<String> title = new Field<>("title");
    private final Field<String> price = new Field<>("price");
    private final Field<String> category = new Field<>("category");
    private final Field<String> stringThumbnail = new Field<>("stringThumbnail");

    public static final String COLLECTION = "liteListings";

    public LiteListing() {
        List<Field> fields = Arrays.asList(listingID, title, price, category, stringThumbnail);
        registerFields(fields);
    }

    public LiteListing(String listingID, String title, String price, String category, String stringThumbnail) {
        this();
        this.listingID.set(listingID);
        this.title.set(title);
        this.price.set(price);
        this.category.set(category);
        this.stringThumbnail.set(stringThumbnail);
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
    public static Task<List<LiteListing>> retrieveAll() {
        return ModelTransaction.fetchAll(COLLECTION, LiteListing.class);
    }
}
