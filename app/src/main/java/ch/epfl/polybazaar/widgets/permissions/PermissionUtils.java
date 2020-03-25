package ch.epfl.polybazaar.widgets.permissions;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;

/**
 * Utility class for access to runtime permissions.
 */
public abstract class PermissionUtils {

    private static final String TAG = "PermissionUtils";

    public static boolean assertPermission(AppCompatActivity currentContext, @NonNull String permission) {
        if (isPermissionGranted(currentContext,permission)) {
            Log.d(TAG, "Permission " + permission + " already granted");
            return true;
        } else {
            Log.d(TAG, "Permission " + permission + " not yet granted");
            requestPermission(currentContext, permission);
            return isPermissionGranted(currentContext,permission);
        }
    }

    private static boolean isPermissionGranted(AppCompatActivity currentContext, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(currentContext, "android.Manifest.permission." + permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(AppCompatActivity currentContext, String permission) {
        int ID = (int)(Math.random()*100000.0);
        if (ActivityCompat.shouldShowRequestPermissionRationale(currentContext, "android.Manifest.permission." + permission)) {
            // Display a dialog with rationale.
            // Here we tell the user why wee need to have this permission
            RationaleDialog.newInstance(ID, false)
                    .show(currentContext.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(currentContext, new String[]{"android.Manifest.permission." + permission}, ID);

        }
    }

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     * @see androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }


}