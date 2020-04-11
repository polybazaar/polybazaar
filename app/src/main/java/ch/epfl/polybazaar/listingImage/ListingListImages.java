package ch.epfl.polybazaar.listingImage;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//This class is used for "edit" functionality
public class ListingListImages implements Serializable {
    private List<String> listStringImage;
    private List<String> imageID;

    public ListingListImages(List<String> list, List<String> IDs) {
        listStringImage = new ArrayList<>();
        imageID = new ArrayList<>();
        if(list != null) {
            listStringImage.addAll(list);
        }
        if(IDs != null) {
            imageID.addAll(IDs);
        }
    }

    public Pair<String[], String[]> getListStringImage() {
        return new Pair(listStringImage.toArray(new String[0]), imageID.toArray(new String[0]));
    }
}
