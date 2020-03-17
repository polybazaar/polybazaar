package ch.epfl.polybazaar.listing;

import static ch.epfl.polybazaar.Utilities.*;

/**
 * A listing represents an object that is listed for sale on the app
 *
 * @author Armen
 *
 */

public class Listing {

    // TODO: add attribute image

    private String title;
    private String description;
    private String price;
    private String userEmail;

    public Listing(String title, String description, String price, String userEmail) {
        this.title = title;
        this.description = description;
        this.price = price;
        if (emailIsValid(userEmail)) {
            this.userEmail = userEmail;
        } else {
            throw new IllegalArgumentException("userEmail has invalid format");
        }
    }

    public String getTitle() {
        if (title == null) return null;
        return title;
    }

    public String getDescription() {
        if (description == null) return null;
        return description;
    }

    public String getPrice() {
        if (price == null) return null;
        return price;
    }

    public String getUserEmail() {
        if (price == null) return null;
        return userEmail;
    }


}
