package ch.epfl.polybazaar.filestorage;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File caching utilities
 */
public final class LocalCache {
    private static String root = "";

    // this class is non-instantiable
    private LocalCache() {}

    /**
     * Returns true if the given resource is cached
     * @param id resource name
     * @param context context
     * @return true if the resource is present in cache
     */
    public static boolean hasFile(String id, Context context) {
        File file = new File(makeFullFilePath(context, id));
        return file.exists();
    }

    /**
     * Add a resource in cache
     * @param id resource name
     * @param context context
     * @return stream on which the data can be written
     * @throws IOException if the file can not be created
     */
    public static OutputStream add(String id, Context context) throws IOException {
        checkRootDirExists(context);
        File cachedFile = new File(makeFullFilePath(context, id));
        cachedFile.createNewFile();

        String s = cachedFile.getAbsolutePath();
        return new FileOutputStream(cachedFile);
    }

    /**
     * Gets a resource from cache
     * @param id resource name
     * @param context context
     * @return stream from which the data can be read
     * @throws IOException if the file cannot be read
     */
    public static InputStream get(String id, Context context) throws IOException {
        File cachedFile = new File(makeFullFilePath(context, id));
        return new FileInputStream(cachedFile);
    }

    /**
     * Sets the folder on which the cache contents are stored
     * @param path relative path of the cache folder
     */
    public static void setRoot(String path) {
        if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
            path += '/';
        }

        root = path;
    }

    private static String makeFullRootPath(Context context) {
        return context.getCacheDir() + File.separator + root;
    }

    private static String makeFullFilePath(Context context, String filename) {
        return makeFullRootPath(context) + filename;
    }

    // Checks that the cache root directory exists and creates it if it does not
    private static void checkRootDirExists(Context context) {
        File rootDir = new File(makeFullRootPath(context));
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new Error("Unable to create cache root directory");
            }
        }
    }

    /**
     * Deletes the cache folder (if it is not the root) and its content
     * @param context context
     */
    public static void cleanUp(Context context) {
        File rootDir = new File(makeFullRootPath(context));

        if (rootDir.exists()) {
            String[] children = rootDir.list();
            for(String child: children){
                File currentFile = new File(rootDir.getPath(), child);
                currentFile.delete();
            }

            if (!root.equals("")) {
                rootDir.delete();
            }
        }
    }
}
