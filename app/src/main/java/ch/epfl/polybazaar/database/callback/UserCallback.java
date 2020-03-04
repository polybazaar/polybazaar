package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.user.User;

public interface UserCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param result the callback content, can be null
     */
    public void onCallback(User result);
}