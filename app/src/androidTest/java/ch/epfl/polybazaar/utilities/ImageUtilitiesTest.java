package ch.epfl.polybazaar.utilities;

import android.graphics.Bitmap;

import org.junit.Test;

import static ch.epfl.polybazaar.utilities.ImageUtilities.cropToSize;
import static ch.epfl.polybazaar.utilities.ImageUtilities.cropToSquare;
import static ch.epfl.polybazaar.utilities.ImageUtilities.getRoundedCroppedBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.limitImageSize;
import static ch.epfl.polybazaar.utilities.ImageUtilities.scaleBitmap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public class ImageUtilitiesTest {

    @Test
    public void resizeBitmapTest() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(300, 500, conf);
        Bitmap output = scaleBitmap(source, 150);
        assertThat(output.getWidth(), is(150));
        assertThat(output.getHeight(), is(250));
        assertNull(scaleBitmap(null, 300));
    }

    @Test
    public void cropToSizeKeepWidthTest() {
        assertNull(cropToSize(null, 300, 500));
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(300, 500, conf);
        Bitmap output = cropToSize(source, 16, 9);
        assertThat(output.getWidth(), is(300));
        assertThat(output.getHeight(), is((int)Math.floor(300 * (double)9/(double)16)));
    }

    @Test
    public void cropToSizeKeepHeightTest() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(500, 300, conf);
        Bitmap output = cropToSize(source, 9, 16);
        assertThat(output.getHeight(), is(300));
        assertThat(output.getWidth(), is((int)Math.floor(300 * (double)9/(double)16)));
    }

    @Test
    public void cropToSquareTest() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(500, 300, conf);
        Bitmap output = cropToSquare(source);
        assertThat(output.getHeight(), is(300));
        assertThat(output.getWidth(), is(300));
    }

    @Test
    public void getRoundCroppedBitmapTest() {
        assertNull(getRoundedCroppedBitmap(null));
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(500, 300, conf);
        Bitmap output = getRoundedCroppedBitmap(source);
        assertThat(output.getHeight(), is(300));
        assertThat(output.getWidth(), is(300));
    }

    @Test
    public void limitSizeTest() {
        assertNull(limitImageSize(null, 12));
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap source = Bitmap.createBitmap(4000, 400, conf);
        Bitmap output = limitImageSize(source, 3000);
        assertThat(output.getHeight(), is(300));
        assertThat(output.getWidth(), is(3000));
    }
}
