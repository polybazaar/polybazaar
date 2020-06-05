package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.user.User;

/**
 * Generic user interface
 */
public interface Account {
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

    /**
     * Updates the user's nickname
     */
    Task<Void> updateNickname(String newNickname);

    /**
     * Updates the user's password
     * @return task
     */
    Task<Void> updatePassword(String newPassword);

    Task<Void> delete();

    /**
     * Deletes the user account along with everything that is attached to the account (listings, messages, etc)
     * @return void task
     */
    default Task<Void> deleteWithDependencies() {
        AuthenticatorFactory.getDependency().signOut();
        List<Task<Void>> deletes = new ArrayList<>();
        getUserData().addOnSuccessListener(user -> {
           for (String s: user.getOwnListings()) {
               deletes.add(Listing.fetch(s).onSuccessTask(Listing::deleteWithDependencies));
           }
           deletes.add(user.delete());
        });
        return Tasks.whenAll(deletes).onSuccessTask(aVoid -> delete());
    }
}
