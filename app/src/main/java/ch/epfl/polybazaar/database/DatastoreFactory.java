package ch.epfl.polybazaar.database;


public abstract class DatastoreFactory {

    private static Datastore dependency;

    public static Datastore getDependency(){
        return dependency;
    }
    public static void setDependency(Datastore datastore){
        dependency = datastore;
    }
}
