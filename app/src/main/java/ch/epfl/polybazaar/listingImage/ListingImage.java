package ch.epfl.polybazaar.listingImage;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentId;

import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.database.Field;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

public class ListingImage extends Model {
    @DocumentId
    private final Field<String> id = new Field<>("id");
    private final Field<String> image = new Field<>("image");
    private final Field<String> refNextImg = new Field<>("refNextImg");

    public static final String COLLECTION = "listingsImage";


    public void setRefNextImg(String refNextImg) {
        this.refNextImg.set(refNextImg);
    }

    // no-argument constructor so that instances can be created by ModelTransaction
    public ListingImage() {
        registerFields(id, image, refNextImg);
    }

    public ListingImage(String image, String refNextImg) {
        this();
        this.image.set(image);
        this.refNextImg.set(refNextImg);
    }

    public String getImage() {
        return image.get();
    }

    public String getRefNextImg() {
        return refNextImg.get();
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
