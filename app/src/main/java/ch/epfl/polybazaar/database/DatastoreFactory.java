package ch.epfl.polybazaar.database;


public class DatastoreFactory {

    private static Datastore dependency;

    public static Datastore getDependency(){
        return dependency;
    }
    public static void setDependency(Datastore datastore){
        dependency = datastore;
    }
}
