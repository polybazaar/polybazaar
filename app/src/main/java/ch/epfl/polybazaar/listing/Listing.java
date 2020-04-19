package ch.epfl.polybazaar.listing;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentId;

import ch.epfl.polybazaar.database.Field;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.Utilities.emailIsValid;
import static ch.epfl.polybazaar.map.MapsActivity.NOLAT;
import static ch.epfl.polybazaar.map.MapsActivity.NOLNG;

/**
 * A listing represents an object that is listed for sale on the app
 *
 * @author Armen
 *
 */
public class Listing extends Model implements Serializable {
    private final Field<String> id = new Field<>("id");
    private final Field<String> title = new Field<>("title");
    private final Field<String> description = new Field<>("description");
    private final Field<String> price = new Field<>("price");
    private final Field<String> userEmail = new Field<>("userEmail");
    private final Field<String> stringImage = new Field<>("stringImage");
    private final Field<String> category = new Field<>("category");
    private final Field<Double> latitude = new Field<>("latitude", NOLAT);
    private final Field<Double> longitude = new Field<>("longitude", NOLNG);

    public static final String COLLECTION = "listings";

    public Listing() {
        registerFields(id, title, description, price, userEmail, stringImage, category, latitude, longitude);
    }

    /**
     *
     * @param title
     * @param description
     * @param price
     * @param userEmail
     * @param stringImage String format : you can use convertFileToString or convertStringToBitmap to convert into String
     */
    public Listing(String title, String description, String price, String userEmail,
                   String stringImage, String category, double latitude, double longitude) throws  IllegalArgumentException {
        this();
        this.title.set(title);
        this.description.set(description);
        this.price.set(price);
        if (emailIsValid(userEmail)) {
            this.userEmail.set(userEmail);
        } else {
            throw new IllegalArgumentException("userEmail has invalid format");
        }
        this.stringImage.set(stringImage);
        this.category.set(category);
        this.latitude.set(latitude);
        this.longitude.set(longitude);
    }

    public Listing(String title, String description, String price, String userEmail, String stringImage, String category) {
        this(title, description, price, userEmail, stringImage, category, NOLAT, NOLNG);
    }

    public Listing(String title, String description, String price, String userEmail, String category) {
        this(title, description, price, userEmail, null, category, NOLAT, NOLNG);
    }

    public String getTitle() {
        return title.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getPrice() {
        return price.get();
    }

    public String getUserEmail() {
        return userEmail.get();
    }

    public String getStringImage() {
        return stringImage.get();
    }

    public String getCategory(){
        return category.get();
    }

    public double getLatitude() {
        return latitude.get();
    }

    public double getLongitude() {
        return longitude.get();
    }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return id.get();
    }

    @Override
    public void setId(String id) {
        this.id.set(id);
    }

    /**
     * Saves the listing with its lite version in a safe way
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public Task<Void> saveWithLiteVersion() {
        return this.save().onSuccessTask(v -> {
            // TODO make thumbnail
           LiteListing liteVersion = new LiteListing(id.get(), title.get(), price.get(), category.get(), "");
           return liteVersion.save();
        });
    }

    /**
     * Fetches the requested listing
     * @param id id of the listing
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static Task<Listing> fetch(String id) {
        return ModelTransaction.fetch(COLLECTION, id, Listing.class);
    }

    /**
     * Deletes a listing in its complete and lite form
     * @param id id of the listing
     * @return task that completes when both versions have been deleted. The task
     * fails if the database is unreachable
     */
    public static Task<Void> deleteWithLiteVersion(String id) {
        Task<Void> deleteListing = ModelTransaction.delete(Listing.COLLECTION, id);
        Task<Void> deleteLiteListing = ModelTransaction.delete(LiteListing.COLLECTION, id);

        return Tasks.whenAll(deleteListing, deleteLiteListing);
    }
}
