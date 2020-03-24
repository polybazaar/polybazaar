package ch.epfl.polybazaar;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

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
        String tempStringImg = "";
        //inspired from https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa/18052269
        if(imageFile != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapFactory.decodeFile(imageFile.getAbsolutePath()).compress(Bitmap.CompressFormat.JPEG, 10, baos);
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
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,10, baos);
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

    public static Object getOrDefaultObj(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

    public static Map<String, Object> getOrDefaultMap(Map<String, Map<String, Object>> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean checkInternetAvailability(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = cm.getAllNetworks();
        boolean hasInternet = false;
        if(networks.length>0){
            for(Network network :networks){
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) hasInternet = true;
            }
        }
        return hasInternet;
    }
}
