package ch.epfl.polybazaar.database.callback;

public interface SuccessCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param result the callback content, can be null
     */
    public void onCallback(boolean result);
}
