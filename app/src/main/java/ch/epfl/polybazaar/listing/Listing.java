package ch.epfl.polybazaar.listing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;

import static ch.epfl.polybazaar.Utilities.convertFileToString;
import static ch.epfl.polybazaar.Utilities.emailIsValid;

/**
 * A listing represents an object that is listed for sale on the app
 *
 * @author Armen
 *
 */

/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public class Listing {

    private String title;
    private String description;
    private String price;
    private String userEmail;
    private String stringImage;
    private String category;

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
}
