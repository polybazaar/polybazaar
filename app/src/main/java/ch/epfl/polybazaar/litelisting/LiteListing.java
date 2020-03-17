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
        if (title == null) return null;
        return title;
    }

    public String getListingID() {
        if (listingID == null) return null;
        return listingID;
    }

    public String getPrice() {
        if (price == null) return null;
        return price;
    }


}
