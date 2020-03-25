package ch.epfl.polybazaar.widgets.permissions;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * Utility class for access to runtime permissions.
 */
public abstract class PermissionUtils extends ActivityCompat implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "PermissionUtils";

    private static SuccessCallback cb;
    private static String denied_msg;
    private static AppCompatActivity context;

    public static void assertPermission(AppCompatActivity currentContext, @NonNull String permission,
                                        @NonNull String message, String denied_message, @NonNull final SuccessCallback callback) {
        if (isPermissionGranted(currentContext,permission)) {
            Log.d(TAG, "Permission " + permission + " already granted");
            callback.onCallback(true);
        } else {
            Log.d(TAG, "Permission " + permission + " not yet granted");
            denied_msg = denied_message;
            cb = callback;
            context = currentContext;
            requestPermission(currentContext, permission, message, denied_message, callback);
        }
    }

    protected static boolean isPermissionGranted(AppCompatActivity currentContext, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(currentContext, "android.permission." + permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(AppCompatActivity currentContext, String permission,
                                          String message, String denied_message, @NonNull final SuccessCallback callback) {
            PermissionRationaleDialog.newInstance(currentContext, false, permission, message, denied_message, callback)
                    .show(currentContext.getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cb.onCallback(true);
        } else {
            PermissionDeniedDialog.newInstance(denied_msg, false).show(context.getSupportFragmentManager(), "permission_denied");
            cb.onCallback(false);
        }
    }
}