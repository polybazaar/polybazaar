package ch.epfl.polybazaar.listingImage;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

public class ListingImage extends Model {
    @DocumentId
    private String id;
    private String image;
    private String refNextImg;

    private static final String COLLECTION = "listingsImage";

    private ListingImage() {}

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
}
