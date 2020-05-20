package ch.epfl.polybazaar.filestorage;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Singleton class for cloud storage with Firebase
 */
public final class FirebaseFileStore implements FileStore {
    private static FirebaseFileStore INSTANCE;
    private final static int MEGABYTE = 1024*1024;

    private FirebaseFileStore() {}

    public static FirebaseFileStore getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseFileStore();
        return INSTANCE;
    }

    @Override
    public Task<Void> delete(String id) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(id);
        return fileReference.delete();
    }

    @Override
    public Task<InputStream> fetch(String id) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(id);
        return fileReference.getBytes(MEGABYTE).onSuccessTask(b -> Tasks.forResult(b == null ? null : new ByteArrayInputStream(b)));
    }

    @Override
    public Task<Void> store(String id, InputStream stream) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(id);
        return fileReference.putStream(stream).onSuccessTask(taskSnapshot -> Tasks.forResult(null));
    }
}
