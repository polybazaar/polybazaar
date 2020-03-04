package ch.epfl.polybazaar.userdatabase;

public interface UserCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param result the callback content, can be null
     */
    public void onCallback(User result);
    public void onCallback(boolean result);
}