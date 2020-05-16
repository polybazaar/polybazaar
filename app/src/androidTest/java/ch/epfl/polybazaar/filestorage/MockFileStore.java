package ch.epfl.polybazaar.filestorage;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class MockFileStore implements FileStore {
    private final static String MOCK_DIR = "cloud-mock" + File.separator;
    private String path;

    /**
     * Sets the context for accessing file system
     * @param context app context
     */
    public void setContext(Context context) {
        File mockDirectory = new File(context.getCacheDir() + File.separator + MOCK_DIR);

        if (!mockDirectory.exists()) {
            mockDirectory.mkdir();
        }

        path = mockDirectory.getAbsolutePath();

        checkMockDirExists();
    }

    /**
     * Deletes the folder used for mocking remote storage
     */
    public void cleanUp() {
        IoUtils.deleteDir(new File(path));
    }

    @Override
    public Task<InputStream> fetch(String id) {
        File file = new File(path + File.separator + id);

        if (file.exists()) {
            try {
                return Tasks.forResult(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new Error("Unexpected IO error");
            }
        } else {
            return Tasks.forResult(null);
        }
    }

    @Override
    public Task<Void> store(String id, InputStream data) {
        try (OutputStream out = new FileOutputStream(new File(path + File.separator + id))) {
            IoUtils.copyStream(data, out);
            return Tasks.forResult(null);
        } catch (IOException e) {
            throw new Error("Unexpected IO error");
        }

    }

    // make sure the folder exists
    private void checkMockDirExists() {
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            if (!rootDir.mkdir()) {
                throw new Error("Unable to create cache root directory");
            }
        }
    }
}