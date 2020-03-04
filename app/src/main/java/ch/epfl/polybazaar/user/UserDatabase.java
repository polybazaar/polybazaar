package ch.epfl.polybazaar.user;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.generic.GenericDatabase;
import ch.epfl.polybazaar.database.callback.SuccessCallbackAdapter;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;
import ch.epfl.polybazaar.database.callback.UserCallback;


public class UserDatabase {

    private static final String TAG = "UserDatabase";

    private GenericDatabase db;

    public UserDatabase(){
        db = new GenericDatabase();
    };

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * @param user the user
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeNewUser(final User user, final SuccessCallback callback) {
        final SuccessCallbackAdapter adapterCallback = new SuccessCallbackAdapter(callback);
        final SuccessCallback intermediateCall = new SuccessCallback() {
            @Override
            public void onCallback(boolean result) {
                if (result == true) {
                    Log.w(TAG, "user email already used");
                    adapterCallback.onCallback(false);
                    return;
                }
                if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                    Log.w(TAG, "user email has invalid format");
                    adapterCallback.onCallback(false);
                    return;
                }
                db.setData("users", user.getEmail(), user, adapterCallback);
            }
        };
        final SuccessCallbackAdapter adapterIntermediateCallback = new SuccessCallbackAdapter(intermediateCall);
        db.fetchData("users", user.getEmail(), adapterIntermediateCallback);
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param callback a callback interface implementation
     */
    public void fetchUser(final String email, final UserCallback callback) {
        final UserCallbackAdapter adapterCallback = new UserCallbackAdapter(callback);
        db.fetchData("users", email, adapterCallback);
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteUser(final String email, final SuccessCallback callback) {
        final SuccessCallbackAdapter adapterCallback = new SuccessCallbackAdapter(callback);
        db.deleteData("user", email, adapterCallback);
    }
}
