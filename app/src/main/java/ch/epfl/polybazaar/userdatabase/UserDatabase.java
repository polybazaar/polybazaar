package ch.epfl.polybazaar.userdatabase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.GenericCallback;
import ch.epfl.polybazaar.database.GenericDatabase;


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
    public void storeNewUser(final User user, final UserCallback callback) {
        final GenericCallback adapterCallback = new GenericCallback() {
            @Override
            public void onCallback(Object result) {
                callback.onCallback((boolean)result);
            }
        };
        GenericCallback intermediateCall = new GenericCallback() {
            @Override
            public void onCallback(Object target) {
                if (((boolean)target) == true) {
                    Log.w(TAG, "user email already used");
                    adapterCallback.onCallback(false);
                    return;
                }
                if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                    Log.w(TAG, "user email has invalid format");
                    adapterCallback.onCallback(false);
                    return;
                }
                db.addData("users", user.getEmail(), adapterCallback);
            }
        };
        db.fetchData("users", user.getEmail(), intermediateCall);
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param callback a callback interface implementation
     */
    public void fetchUser(final String email, final UserCallback callback) {
        final GenericCallback adapterCallback = new GenericCallback() {
            @Override
            public void onCallback(Object result) {
                callback.onCallback((User)result);
            }
        };
        db.fetchData("users", email, adapterCallback);
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteUser(final String email, final UserCallback callback) {
        final GenericCallback adapterCallback = new GenericCallback() {
            @Override
            public void onCallback(Object result) {
                callback.onCallback((boolean)result);
            }
        };
        db.deleteData("user", email, adapterCallback);
    }
}
