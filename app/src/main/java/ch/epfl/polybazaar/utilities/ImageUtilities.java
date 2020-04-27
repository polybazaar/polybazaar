package ch.epfl.polybazaar.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Provides utilities to manipulate images
 */
public final class ImageUtilities {
    // class is non-instantiable
    private ImageUtilities() {}


    /**
     * Convert a File to a String
     * @param imageFile
     * @return a String
     */
    public static String convertFileToString(File imageFile) {
        return convertFileToStringWithQuality(imageFile, 10);
    }

    /**
     * Convert a File to a String with a specific compression quality
     * @param imageFile
     * @return a String
     */
    public static String convertFileToStringWithQuality(File imageFile, int quality) {
        String tempStringImg = "";
        // inspired from https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa/18052269
        if(imageFile != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapFactory.decodeFile(imageFile.getAbsolutePath()).compress(Bitmap.CompressFormat.JPEG, quality, baos);
            tempStringImg = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }
        return tempStringImg;
    }

    /**
     * Convert a String image to a Bitmap
     * @param stringImage
     * @return a Bitmap
     */
    public  static Bitmap convertStringToBitmap (String stringImage) {
        if(stringImage == null || stringImage.equals("")) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            byte [] encodeByte = Base64.decode(stringImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            //do nothing
        }
        return bitmap;
    }

    /**
     * Convert Bitmap to String
     * @param bitmap
     * @return a String
     * taken from Stackoverflow
     */
    public static String convertBitmapToString(Bitmap bitmap){
        return convertBitmapToStringWithQuality(bitmap, 10);
    }

    /**
     * Convert Bitmap to String with a specific compression quality
     * @param bitmap
     * @return a String
     * taken from Stackoverflow
     */
    public static String convertBitmapToStringWithQuality(Bitmap bitmap, int quality){
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * Convert a PNG Bitmap to String
     * @param bitmap
     * @return a String
     * taken from Stackoverflow
     */
    public static String convertBitmapToStringPNG(Bitmap bitmap){
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap convertDrawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Resize String image, return null if input is null or equals to ""
     * @param input
     * @return
     */
    public static String resizeStringImageThumbnail(String input) {
        if(input == null || input.equals("")) {
            return null;
        }
        Bitmap b = convertStringToBitmap(input);
        int width = b.getWidth() * 240 / b.getHeight();
        return convertBitmapToStringWithQuality(Bitmap.createScaledBitmap(b, width, 240, false), 100);
    }

    /**
     * Helper function to resize a bitmap given in argument
     * @param bitmap original image
     * @param scaleWidth scale factor for width, should be between 0 and 1
     * @param scaleHeight scale factor for height, should be between 0 and 1
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) throws IllegalArgumentException{
        if(bitmap == null) {
            return null;
        }
        if(scaleWidth < 0 || scaleWidth > 1 || scaleHeight < 0 || scaleHeight > 1) {
            throw new IllegalArgumentException("Scale factors should be between 0 and 1");
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * Crops a bitmap to a given ratio centered on the image
     * @param bitmap the bitmap
     * @param sizeX horizontal target length, in length units, relative
     * @param sizeY vertical target length, in length units, relative
     * @return the cropped bitmap
     */
    public static Bitmap cropToSize(Bitmap bitmap, int sizeX, int sizeY) {
        if (bitmap != null) {
            double targetRatio = ((double) sizeX) / ((double) sizeY);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            double imageRatio = ((double) width) / ((double) height);
            int targetWidth;
            int targetHeight;
            if ((imageRatio >= 1 && targetRatio >= 1 && targetRatio >= imageRatio)     // Landscape into Landscape wide
                    || (imageRatio < 1 && targetRatio >= 1)                             // Portrait into Landscape
                    || (imageRatio < 1 && targetRatio >= 1 && targetRatio >= imageRatio)) // Portrait into Portrait wide
            {
                targetWidth = width;
                targetHeight = (int) Math.floor(targetWidth * (1.0 / targetRatio));
                targetHeight = Math.min(targetHeight, height);
            } else {
                targetHeight = height;
                targetWidth = (int) Math.floor(targetHeight * (targetRatio));
                targetWidth = Math.min(targetWidth, width);
            }
            int centerX = width >> 1;
            int centerY = height >> 1;
            return Bitmap.createBitmap(bitmap, centerX - (targetWidth >> 1), centerY - (targetHeight >> 1),
                    targetWidth, targetHeight);
        } else {
            return null;
        }
    }

    /**
     * Crops a bitmap to a square centered on the image
     * @param bitmap the bitmap
     * @return the cropped bitmap
     */
    public static Bitmap cropToSquare(Bitmap bitmap) {
        return cropToSize(bitmap, 1, 1);
    }

    /**
     * Crop a bitmap to circle centered on the image
     * @param bitmap the bitmap
     * @return the cropped bitmap
     */
    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = cropToSquare(bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int centerX = width >> 1;
            int centerY = height >> 1;
            int radius = (width < height) ? centerX : centerY;
            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paintColor = new Paint();
            paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
            canvas.drawCircle(centerX, centerY, radius, paintColor);
            Paint paintImage = new Paint();
            paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(bitmap, 0, 0, paintImage);
            return output;
        } else {
            return null;
        }
    }

    /**
     * Scales the resolution of a bitmap
     * @param bitmap the source bitmap
     * @param targetWidth the desired width, in pixels
     * @return the scaled bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int targetWidth) {
        if (bitmap != null) {
            double ratio = ((double) bitmap.getHeight()) / ((double) bitmap.getWidth());
            int widthFactor = (int) Math.floor(((double) targetWidth) / ((double) bitmap.getWidth()));
            int heightFactor = (int) Math.floor((((double) targetWidth) * ratio) / ((double) bitmap.getHeight()));
            return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * widthFactor,
                    bitmap.getHeight() * heightFactor, true);
        } else {
            return null;
        }
    }

}
