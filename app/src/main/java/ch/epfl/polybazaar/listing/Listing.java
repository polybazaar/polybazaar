package ch.epfl.polybazaar.listing;
import java.io.Serializable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentId;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.Utilities.emailIsValid;

/**
 * A listing represents an object that is listed for sale on the app
 *
 * @author Armen
 *
 */
public class Listing extends Model implements Serializable {
    @DocumentId
    private String id;
    private String title;
    private String description;
    private String price;
    private String userEmail;
    private String stringImage;
    private String category;

    public static final String COLLECTION = "listings";

    public Listing() {}

    /**
     *
     * @param title
     * @param description
     * @param price
     * @param userEmail
     * @param stringImage String format : you can use convertFileToString or convertStringToBitmap to convert into String
     */
    public Listing(String title, String description, String price, String userEmail, String stringImage, String category) {
        this.title = title;
        this.description = description;
        this.price = price;
        if (emailIsValid(userEmail)) {
            this.userEmail = userEmail;
        } else {
            throw new IllegalArgumentException("userEmail has invalid format");
        }
        this.stringImage = stringImage;
        this.category = category;
    }

    public Listing(String title, String description, String price, String userEmail, String category) {
        this(title, description, price, userEmail, null, category);
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getStringImage() {
        return stringImage;
    }

    public String getCategory(){
        return category;
    }

    @Override
    public String collectionName() {
        return "listings";
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
