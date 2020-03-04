package ch.epfl.polybazaar.userdatabase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.GenericCallback;
import ch.epfl.polybazaar.database.GenericDatabase;

/**
 * This class is meant to be used as an observable,
 * which is why all methods need to specify an observer when they are called.
 * Once the query is computed and available, the observer will be notified.
 * The functions isSuccess() and getFetchedUser() can THEN be used to read the query results.
 */
public class UserDatabase {

    private static final String TAG = "UserDatabase";

    private GenericDatabase db;

    public UserDatabase(boolean useFirestore){
        db = new GenericDatabase(useFirestore);
    };

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * @param user the user
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeNewUser(final User user, final GenericCallback callback) {
        GenericCallback intermediateCall = new GenericCallback() {
            @Override
            public void onCallback(Object target) {
                if (((boolean)target) == true) {
                    Log.w(TAG, "user email already used");
                    callback.onCallback(false);
                    return;
                }
                if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                    Log.w(TAG, "user email has invalid format");
                    callback.onCallback(false);
                    return;
                }
                db.addData("users", user.getEmail(), callback);
            }
        };
        db.fetchData("users", user.getEmail(), intermediateCall);
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param callback a callback interface implementation
     */
    public void fetchUser(final String email, final GenericCallback callback) {
        db.fetchData("users", email, callback);
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param fb the database
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteUser(final String email, final GenericCallback callback) {
        db.deleteData("user", email, callback);
    }
}
