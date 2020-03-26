package ch.epfl.polybazaar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.user.User;

public abstract class Utilities {

    public static Map<String, Object> getMap(Object o) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (o instanceof Listing) {
            result.put("description", ((Listing)o).getDescription());
            result.put("price", ((Listing)o).getPrice());
            result.put("title", ((Listing)o).getTitle());
            result.put("userEmail", ((Listing)o).getUserEmail());
        } else if (o instanceof LiteListing) {
            result.put("listingID", ((LiteListing)o).getListingID());
            result.put("price", ((LiteListing)o).getPrice());
            result.put("title", ((LiteListing)o).getTitle());
            result.put("category", ((LiteListing)o).getCategory());
            result.put("stringThumbnail", ((LiteListing)o).getStringThumbnail());
        } else if (o instanceof User) {
            result.put("email", ((User)o).getEmail());
            result.put("nickName", ((User)o).getNickName());
        } else {
            return null;
        }
        return result;
    }

    // TODO: can be adjusted
    public static boolean nameIsValid(String name) {
        //return (name.matches("[a-zA-Z]+"));
        return true;
    }

    public static boolean emailIsValid(String email) {
        return (email.matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"));
    }

    public static boolean isValidUser(User user) {
        if (user != null
        && nameIsValid(user.getNickName())
        && emailIsValid(user.getEmail())) {
            return true;
        }
        return false;
    }

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
        //inspired from https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa/18052269
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
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }



    public static Bitmap convertDrawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

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
     * Helper function to resize a bitmap given in argument
     * @param bitmap original image
     * @param scaleWidth scale factor for width, should be between 0 and 1
     * @param scaleHeight scale factor for height, should be between 0 and 1
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) throws IllegalArgumentException{

        if(scaleWidth < 0 || scaleWidth > 1 || scaleHeight < 0 || scaleHeight > 1) {
            throw new IllegalArgumentException("Scale factors should be between 0 and 1");
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }


    public static Object getOrDefaultObj(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

    public static Map<String, Object> getOrDefaultMap(Map<String, Map<String, Object>> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }
}
