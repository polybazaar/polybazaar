package ch.epfl.polybazaar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.message.ChatMessage;
import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

public abstract class Utilities {

    // TODO: can be adjusted
    public static boolean nameIsValid(String name) {
        //return (name.matches("[a-zA-Z]+"));
        return true;
    }

    public static boolean emailIsValid(String email) {
        return (email.matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"));
    }

    public static boolean passwordIsValid(String password) {
        return password.length() >= 6;
    }

    public static boolean nickNameIsValid(String nickname) {
        return nickname.length() >= 6;
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
     * Helper method to retrieve the logged user (if any)
     * @return the logged user (can be null)
     */
    public static AppUser getUser() {
        Authenticator auth = AuthenticatorFactory.getDependency();
        AppUser user = auth.getCurrentUser();
        return user;
    }

    /**
     * Helper method to display a toast
     * @param context the context in which the toast has to be displayed
     * @param resId the ID of the string to be displayed
     * @param gravity where on screen to display the toast
     */
    public static void displayToast(Context context, int resId, int gravity) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }


    /**
     * Helper method to check is a user is logged in and display an error message
     * @param context
     * @return
     */
    public static boolean checkUserLoggedIn(Context context) {
        AppUser user = getUser();

        // the user is not logged in
        if (user == null) {
            displayToast(context, R.string.sign_in_required, Gravity.CENTER);
            return false;
        } else {
            return true;
        }
    }

}