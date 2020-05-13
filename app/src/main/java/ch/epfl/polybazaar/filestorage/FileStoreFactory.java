package ch.epfl.polybazaar.filestorage;

/**
 * Factory class that enables cloud storage dependency injection
 */
public final class FileStoreFactory {
    private static FileStore dependency = FirebaseFileStore.getInstance();

    // this class is non-instantiable
    private FileStoreFactory() {}

    /**
     * Gets the dependency
     * @return dependency
     */
    public static FileStore getDependency() {
        return dependency;
    }

    /**
     * Sets the dependency
     * @param fileStore dependency to use
     */
    public static void setDependency(FileStore fileStore) {
       dependency = fileStore;
    }
}
