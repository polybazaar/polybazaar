package ch.epfl.polybazaar.database.datastore;


import ch.epfl.polybazaar.database.datastore.firebase.FirebaseDataStore;
import ch.epfl.polybazaar.database.datastore.mock.MockDataStore;
import ch.epfl.polybazaar.filestorage.FileStoreFactory;
import ch.epfl.polybazaar.filestorage.LocalCache;

public abstract class DataStoreFactory {

    private static DataStore dependency = new FirebaseDataStore();

    public static DataStore getDependency(){
        return dependency;
    }

    /**
     * Sets up a new and empty MockDataStore database
     */
    public static void useMockDataStore(){
        dependency = new MockDataStore();
    }

}
