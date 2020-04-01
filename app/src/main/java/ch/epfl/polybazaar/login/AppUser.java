package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

import ch.epfl.polybazaar.database.callback.UserCallback;
import ch.epfl.polybazaar.user.User;

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
     * Fetches the database record corresponding to the user
     * @return task containing the user
     */
    Task<User> getUserData();

    /**
     * Gets the user's email address
     * @return email address
     */
    String getEmail();

    /**
     * Gets the user's nickname
     * @return nickname
     */
    String getNickname();
}
