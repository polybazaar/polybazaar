package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

/**
 * Authentication interface
 */
public interface Authenticator {
    /**
     * Returns the user currently signed in
     * @return user
     */
    AppUser getCurrentUser();

    /**
     * Attempts to sign in the user with the given credentials
     * @param email user's email
     * @param password user's password
     * @return task with the result of the sign in attempt
     */
    Task<AuthenticatorResult> signIn(String email, String password);

    /**
     * Attempts to create a user with the given credentials
     * @param email user's email
     * @param password user's password
     * @return task with the result of the registering attempt
     */
    Task<AuthenticatorResult> createUser(String email, String nickname, String password);

    /**
     * Signs the user out of the app
     */
    void signOut();
}
