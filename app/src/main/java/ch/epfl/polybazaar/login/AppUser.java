package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

import ch.epfl.polybazaar.database.callback.UserCallback;

/**
 * Generic user interface
 */
public interface AppUser {
    /**
     * Checks that the user's email has been verified
     * @return true iff the email is verified
     */
    boolean isEmailVerified();

    /**
     * Sends an message to the user's email address for verification
     * @return task
     */
    Task<Void> sendEmailVerification();

    /**
     * Reloads the user's properties
     * Should be called before checking if the user's email has been verified correctly
     * @return task
     */
    Task<Void> reload();

    /**
     * Starts fetching the database record corresponding to the user
     * @param cb callback containing the user record
     */
    void getUserData(UserCallback cb);
}
