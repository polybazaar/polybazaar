package ch.epfl.polybazaar.listingImage;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentId;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;

public class ListingImage extends Model {
    @DocumentId
    private String id;
    private String image;
    private String refNextImg;

    public static final String COLLECTION = "listingsImage";

    public ListingImage() {}

    public ListingImage(String image, String refNextImg) {
        this.image = image;
        this.refNextImg = refNextImg;
    }

    public String getImage() {
        return image;
    }

    public String getRefNextImg() {
        return refNextImg;
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
     * Fetches the requested image
     * @param id id of the image
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static Task<ListingImage> fetch(String id) {
        return ModelTransaction.fetch(COLLECTION, id, ListingImage.class);
    }

    /**
     * Deletes a ListingImage in its complete and lite form
     * @param id id of the listingImage
     * @return task that completes when both versions have been deleted. The task
     * fails if the database is unreachable
     */
    public static Task<Void> delete(String id) {
        Task<Void> deleteImage = ModelTransaction.delete(ListingImage.COLLECTION, id);

        return Tasks.whenAll(deleteImage);
    }
}
