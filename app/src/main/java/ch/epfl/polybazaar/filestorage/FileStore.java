package ch.epfl.polybazaar.filestorage;

import com.google.android.gms.tasks.Task;

import java.io.InputStream;

/**
 * Offers methods to communicate with cloud storage
 */
public interface FileStore {
    /**
     * Fetch a resource from the cloud
     * @param id resource name
     * @return task containing a stream with the data, null if the resource does not exist
     */
    Task<InputStream> fetch(String id);

    /**
     * Store a resource on the cloud
     * @param id resource name
     * @param data stream from which the data can be read
     * @return void task
     */
    Task<Void> store(String id, InputStream data);

    /**
     * Deletes a resource from the cloud
     * @param id resource name
     * @return void task
     */
    Task<Void> delete(String id);
}
