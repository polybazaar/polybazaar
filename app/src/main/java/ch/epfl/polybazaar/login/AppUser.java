package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

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
}
