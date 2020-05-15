package ch.epfl.polybazaar.listing;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.SimpleField;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
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

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String USER_EMAIL = "userEmail";
    public static final String STRING_IMAGE = "stringImage";
    public static final String CATEGORY = "category";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String VIEWS = "views";
    public static final String HAVE_SEEN_USERS = "haveSeenUsers";
    public static final String LISTING_ACTIVE = "listingActive";
    public static final String IMAGES_REFS = "imagesRefs";

    private final SimpleField<String> id = new SimpleField<>(ID);
    private final SimpleField<String> title = new SimpleField<>(TITLE);
    private final SimpleField<String> description = new SimpleField<>(DESCRIPTION);
    private final SimpleField<String> price = new SimpleField<>(PRICE);
    private final SimpleField<String> userEmail = new SimpleField<>(USER_EMAIL);
    private final SimpleField<String> stringImage = new SimpleField<>(STRING_IMAGE);
    private final SimpleField<String> category = new SimpleField<>(CATEGORY);
    private final SimpleField<Double> latitude = new SimpleField<>(LATITUDE, NOLAT);
    private final SimpleField<Double> longitude = new SimpleField<>(LONGITUDE, NOLNG);
    private final SimpleField<Long> views = new SimpleField<>(VIEWS);
    private final SimpleField<String> haveSeenUsers = new SimpleField<>(HAVE_SEEN_USERS);
    private final SimpleField<Boolean> listingActive = new SimpleField<>(LISTING_ACTIVE);
    private final SimpleField<List<String>> imagesRefs = new SimpleField<>(IMAGES_REFS, new ArrayList<>());

    public static final String SOLD = "SOLD";
    public static final String COLLECTION = "listings";

    // no-argument constructor so that instances can be created by ModelTransaction
    public Listing() {
        registerFields(id, title, description, price, userEmail, stringImage, category, latitude, longitude, views, haveSeenUsers, listingActive, imagesRefs);
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
        this.views.set(0L);
        this.haveSeenUsers.set("");
        this.listingActive.set(true);
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

    public String getHaveSeenUsers(){
        return haveSeenUsers.get();
    }

    public long getViews(){
        return views.get();
    }

    public Boolean getListingActive() {
        if (listingActive.get() == null) {
            return true;
        }
        return listingActive.get();
    }

    public List<String> getImagesRefs() {
        return imagesRefs.get();
    }

    public Task<List<Bitmap>> fetchImages(Context ctx) {
        List<Task<Bitmap>> tasks = new ArrayList<>();
        for (String ref: imagesRefs.get()) {
            tasks.add(ImageTransaction.fetch(ref, ctx));
        }

        return Tasks.whenAllSuccess(tasks);
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

    public static <T> Task<Void> updateField(String field, String id, T updatedValue) {
        return ModelTransaction.updateField(COLLECTION, id, field, updatedValue);
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

    public static Task<Void> updateMultipleFields(String id, Map<String, Object> updated){
       return ModelTransaction.updateMultipleFields(Listing.COLLECTION, id, updated);
    }
}
