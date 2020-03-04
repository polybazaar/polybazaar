package ch.epfl.polybazaar.database.generic;

public interface GenericCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param result the callback content, can be null
     */
    public void onCallback(Object result);
}
