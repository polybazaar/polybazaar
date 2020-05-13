package ch.epfl.polybazaar.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities related to IO
 */
public final class IoUtils {
    private final static int BUFFER_SIZE = 1024;

    // this class is non-instantiable
    private IoUtils() {}

    /**
     * Copy an input stream to an output stream
     * @param input source stream
     * @param output destination stream
     */
    public static void copyStream(InputStream input, OutputStream output) {
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

    /**
     * Deletes a directory and its content recursively. If the given file is not a dir or
     * or does not exist, this method does nothing.
     * @param dir path of the directory
     */
    public static void deleteDir(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            String[] children = dir.list();
            for(String child: children){
                File currentFile = new File(dir.getPath(), child);

                if (currentFile.isDirectory()) {
                    deleteDir(currentFile);
                }

                currentFile.delete();
            }
            dir.delete();
        }
    }
}
