package ch.epfl.polybazaar.userListings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import java.util.ArrayList;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.login.AppUser;

import static ch.epfl.polybazaar.Utilities.displayToast;
import static ch.epfl.polybazaar.Utilities.getUser;

public abstract class Favorites {

    /**
     * Display favorites of the user (if any)
     * @param context the context from which the method is called
     */
    public static void displayFavorites(Context context) {
        AppUser user = getUser();

        user.getUserData().addOnSuccessListener(authUser -> {
            ArrayList<String> favoritesIds = authUser.getFavorites();

            // the list of favorites of the user is empty
            if (favoritesIds == null || favoritesIds.isEmpty()) {
                displayToast(context, R.string.no_favorites, Gravity.CENTER);

                // we relaunch the activity with the list of favorites in the bundle
            } else {
                Intent intent = new Intent(context, SalesOverview.class);
                Bundle extras = new Bundle();
                extras.putStringArrayList("userLiteListings", favoritesIds);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });


    }

}
