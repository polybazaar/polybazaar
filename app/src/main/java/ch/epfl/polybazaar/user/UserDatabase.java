package ch.epfl.polybazaar.user;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;
import ch.epfl.polybazaar.database.callback.UserCallback;


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

    public static final String userCollectionName = "users";

    private static DataStore db;

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * callback will contain true if successful, false otherwise
     * @param user the user
     * @param callback a SuccessCallback interface implementation
     */
    public static void storeNewUser(final User user, final SuccessCallback callback) {
        db = DataStoreFactory.getDependency();

        final UserCallback intermediateCall = new UserCallback() {
            @Override
            public void onCallback(User result) {
                if (result!=null && result.getID().equals(user.getID())) {
                        Log.w(TAG, "user email already used");
                        callback.onCallback(false);
                        return;
                }
                if (!(user.getID().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"))) {
                    Log.w(TAG, "user email has invalid format");
                    callback.onCallback(false);
                    return;
                }
                db.setData(userCollectionName, user.getID(), user, callback);
            }
        };
        final UserCallbackAdapter adapterIntermediateCallback = new UserCallbackAdapter(intermediateCall);
        db.fetchData(userCollectionName, user.getID(), adapterIntermediateCallback);
    }

    /**
     * Fetch a user from the database
     *  callback will contain the user
     * @param email the user to fetch's email
     * @param callback a UserCallback interface implementation
     */
    public static void fetchUser(final String email, final UserCallback callback) {
        String newEmail = email.replace("@epfl.ch", "");
        db = DataStoreFactory.getDependency();
        final UserCallbackAdapter adapterCallback = new UserCallbackAdapter(callback);
        db.fetchData(userCollectionName, newEmail, adapterCallback);
    }

     /**
     * Deletes a user from the database
     * callback will contain true if successful, false otherwise
     * @param email the users email address
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteUser(final String email, final SuccessCallback callback) {
        String newEmail = email.replace("@epfl.ch", "");
        db = DataStoreFactory.getDependency();
        db.deleteData(userCollectionName, newEmail, callback);
    }

    /**
     * Performs a query which returns all user IDs where their field == equalTo
     * @param field the field to be checked for equality
     * @param equalTo what field should be equal to
     * @param callback a StringListCallback interface implementation
     */
    public static void queryUserStringEquality(final String field, final String equalTo, final StringListCallback callback) {
        /*String newEqualTo = equalTo;
        if (field.equals("email")) {
            newEqualTo = equalTo.replace("@epfl.ch", "");
        }
        */
        db = DataStoreFactory.getDependency();
        db.queryStringEquality(userCollectionName, field, equalTo, callback);
    }
}
