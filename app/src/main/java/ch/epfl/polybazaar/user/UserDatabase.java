package ch.epfl.polybazaar.user;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;
import ch.epfl.polybazaar.database.callback.UserCallback;

import static ch.epfl.polybazaar.database.generic.GenericDatabase.*;


/**
 * Usage Example:
 *
 * UserCallback callbackUser = new UserCallback() {
 *             // onCallback is executed once the data request has completed
 *             @Override
 *             public void onCallback(User result) {
 *                 // use result;
 *             }
 *         };
 * fetchUser("jean.chappy@epfl.ch", callbackUser);
 */
public abstract class UserDatabase {

    private static final String TAG = "UserDatabase";

    private static final String userCollectionName = "users";

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * callback will contain true if successful, false otherwise
     * @param user the user
     * @param callback a SuccessCallback interface implementation
     */
    public static void storeNewUser(final User user, final SuccessCallback callback) {
        final UserCallback intermediateCall = new UserCallback() {
            @Override
            public void onCallback(User result) {
                if (result!=null && result.getEmail().equals(user.getEmail())) {
                        Log.w(TAG, "user email already used");
                        callback.onCallback(false);
                        return;
                }
                if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                    Log.w(TAG, "user email has invalid format");
                    callback.onCallback(false);
                    return;
                }
                setData(userCollectionName, user.getEmail(), user, callback);
            }
        };
        final UserCallbackAdapter adapterIntermediateCallback = new UserCallbackAdapter(intermediateCall);
        fetchData(userCollectionName, user.getEmail(), adapterIntermediateCallback);
    }

    /**
     * Fetch a user from the database
     *  callback will contain the user
     * @param email the user to fetch's email
     * @param callback a UserCallback interface implementation
     */
    public static void fetchUser(final String email, final UserCallback callback) {
        final UserCallbackAdapter adapterCallback = new UserCallbackAdapter(callback);
        fetchData(userCollectionName, email, adapterCallback);
    }

    /**
     * Deletes a user from the database
     * callback will contain true if successful, false otherwise
     * @param email the users email address
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteUser(final String email, final SuccessCallback callback) {
        deleteData(userCollectionName, email, callback);
    }
}