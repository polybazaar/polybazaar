package ch.epfl.sdp;

/**
 * A listing represents an object that is listed for sale on the app
 *
 * @author Armen
 *
 */

public class Listing {

    // TODO: add attribute image
    // TODO: add attribute of type user (seller)

    private String title;
    private String description;
    private String price;


    public Listing(String title, String description, String price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPrice() {
        return this.price;
    }


}
