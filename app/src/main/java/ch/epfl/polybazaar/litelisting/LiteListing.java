package ch.epfl.polybazaar.litelisting;

public class LiteListing {

    // TODO: add attribute thumbnail

    private String listingID;
    private String title;
    private String price;


    public LiteListing(String listingID, String title, String price) {
        this.title = title;
        this.listingID = listingID;
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public String getListingID() {
        return this.listingID;
    }

    public String getPrice() {
        return this.price;
    }


}
