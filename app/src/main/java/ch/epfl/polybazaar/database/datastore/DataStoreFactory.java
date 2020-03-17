package ch.epfl.polybazaar.database.datastore;


public abstract class DataStoreFactory {

    private static DataStore dependency;

    public static DataStore getDependency(){
        return dependency;
    }

    public static void setDependency(DataStore datastore){
        dependency = datastore;
    }

}
