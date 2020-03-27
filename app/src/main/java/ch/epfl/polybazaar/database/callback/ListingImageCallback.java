package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.listingImage.ListingImage;

public interface ListingImageCallback {

    void onCallback(ListingImage result);
}
