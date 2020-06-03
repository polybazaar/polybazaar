package ch.epfl.polybazaar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;

import ch.epfl.polybazaar.utilities.ImageUtilities;

import static com.google.common.collect.Range.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ImageUtilitiesTest {

    @Test
    public void testBitmapToStringNull(){
        assertNull(ImageUtilities.convertBitmapToString(null));
    }

    @Test
    public void testBitmapToStringNonNull(){
        Drawable d = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {

            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
        assertNull(ImageUtilities.convertDrawableToBitmap(d));
    }
}
