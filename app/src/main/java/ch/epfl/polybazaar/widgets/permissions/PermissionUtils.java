package ch.epfl.polybazaar.widgets.permissions;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * Utility class for access to runtime permissions.
 */
public abstract class PermissionUtils {

    private static final String TAG = "PermissionUtils";

    public static void assertPermission(AppCompatActivity currentContext, @NonNull String permission,
                                        @NonNull String message, @NonNull final SuccessCallback callback) {
        if (isPermissionGranted(currentContext,permission)) {
            Log.d(TAG, "Permission " + permission + " already granted");
            callback.onCallback(true);
        } else {
            Log.d(TAG, "Permission " + permission + " not yet granted");
            requestPermission(currentContext, permission, message, callback);
        }
    }

    protected static boolean isPermissionGranted(AppCompatActivity currentContext, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(currentContext, "android.permission." + permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(AppCompatActivity currentContext, String permission,
                                          String message, @NonNull final SuccessCallback callback) {
            PermissionRationaleDialog.newInstance(false, permission, message, callback)
                    .show(currentContext.getSupportFragmentManager(), "dialog");
    }

}