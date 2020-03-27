package ch.epfl.polybazaar.listingImage;

public class ListingImage {

    private String image;
    private String refNextImg;

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
}
