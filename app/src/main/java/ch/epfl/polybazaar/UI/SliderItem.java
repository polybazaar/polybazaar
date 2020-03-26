package ch.epfl.polybazaar.UI;

import android.graphics.Bitmap;

public class SliderItem {

    //change here for a bitmap
    private Bitmap image;

    public SliderItem(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

}
