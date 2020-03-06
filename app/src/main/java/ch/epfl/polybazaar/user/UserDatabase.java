package ch.epfl.polybazaar.user;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.generic.GenericDatabase;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;
import ch.epfl.polybazaar.database.callback.UserCallback;


/**
 * Usage Example:
 *
 * private UserDatabase udb = new UserDatabase();
 * UserCallback callbackUser = new UserCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(User result) {
 *                 // use result;
 *             }
 *         };
 * udb.fetchUser("jean.chappy@epfl.ch", callbackUser);
 */
public class UserDatabase {

    private static final String TAG = "UserDatabase";

    private GenericDatabase db;

    public UserDatabase(){
        db = new GenericDatabase();
    };

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * callback will contain true if successful, false otherwise
     * @param user the user
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeNewUser(final User user, final SuccessCallback callback) {
        final UserCallback intermediateCall = new UserCallback() {
            @Override
            public void onCallback(User result) {
                if (result!=null) {
                    if (result.getEmail().equals(user.getEmail())) {
                        Log.w(TAG, "user email already used");
                        callback.onCallback(false);
                        return;
                    }
                }
                if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                    Log.w(TAG, "user email has invalid format");
                    callback.onCallback(false);
                    return;
                }
                db.setData("users", user.getEmail(), user, callback);
            }
        };
        final UserCallbackAdapter adapterIntermediateCallback = new UserCallbackAdapter(intermediateCall);
        db.fetchData("users", user.getEmail(), adapterIntermediateCallback);
    }

    /**
     * Fetch a user from the database
     *  callback will contain the user
     * @param email the user to fetch's email
     * @param callback a callback interface implementation
     */
    public void fetchUser(final String email, final UserCallback callback) {
        final UserCallbackAdapter adapterCallback = new UserCallbackAdapter(callback);
        db.fetchData("users", email, adapterCallback);
    }

    /**
     * Deletes a user from the database
     * callback will contain true if successful, false otherwise
     * @param email the users email address
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteUser(final String email, final SuccessCallback callback) {
        db.deleteData("user", email, callback);
    }
}
