package ch.epfl.polybazaar.filestorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ImageTransaction {
    private static final int BUFFER_SIZE = 1024;

    // this class is non-instantiable
    private ImageTransaction(){}

    /**
     * Fetch an bitmap from cloud storage. If the bitmap is cached, it is retrieved directly from
     * the cache without querying the remote storage.
     * @param id file name
     * @param context context
     * @return task containing the requested bitmap, or null if it does not exist
     */
    public static Task<Bitmap> fetch(String id, Context context) {
        if (LocalCache.hasFile(id, context)) {
            try (InputStream stream = LocalCache.get(id, context)) {
                return Tasks.forResult(BitmapFactory.decodeStream(stream));
            } catch (IOException e) {
                throw new Error("Unexpected IO error");
            }
        } else {
            FileStore fileStore = FileStoreFactory.getDependency();
            return fileStore.fetch(id).onSuccessTask(inputStream -> {

                // file does not exist
                if (inputStream == null) {
                    return Tasks.forResult(null);
                }

                // store result in cache
                OutputStream outputStream = LocalCache.add(id, context);
                copyStream(inputStream, outputStream);

                // re-read from cache since an InputStream cannot be read twice
                InputStream cacheInputStream = LocalCache.get(id, context);

                return Tasks.forResult(BitmapFactory.decodeStream(cacheInputStream));
            });
        }
    }

    /**
     * Store a bitmap on the cloud storage. The bitmap is cached before uploading the data to the
     * cloud
     * @param id file name
     * @param bitmap bitmap to store
     * @param quality quality of compression
     * @param context context
     * @return void task
     */
    public static Task<Void> store(String id, Bitmap bitmap, int quality, Context context) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        // write image to local cache
        try (OutputStream outputStream = LocalCache.add(id, context)) {
            byteArrayOutputStream.writeTo(outputStream);
        } catch (IOException e) {
            throw new Error("Unexpected IO error");
        }

        // upload image to cloud
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        FileStore fileStore = FileStoreFactory.getDependency();
        return fileStore.store(id, byteArrayInputStream);
    }

    // read bytes from input stream and write them to output stream
    private static void copyStream(InputStream input, OutputStream output) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new Error("Unexpected IO error");
        }
    }
}
