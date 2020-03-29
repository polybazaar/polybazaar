package ch.epfl.polybazaar.widgets.permissions;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * Utility class for access to runtime permissions.
 */
public final class PermissionUtils extends AppCompatActivity{

    private static final String TAG = "PermissionUtils";

    private String permission;
    private String message;
    private String denied_message;
    private SuccessCallback callback;

    private PermissionUtils(@NonNull String permission,
                           @NonNull String message, String denied_message, @NonNull final SuccessCallback callback) {
        this.callback = callback;
        this.denied_message =denied_message;
        this.message = message;
        this.permission = permission;
    }

    public static PermissionUtils createPermissionRequest(@NonNull String permission,
                                                          @NonNull String message, String denied_message,
                                                          @NonNull final SuccessCallback callback) {
        return new PermissionUtils(permission, message, denied_message, callback);
    }

    public void assertPermission() {
        if (isPermissionGranted(permission)) {
            Log.d(TAG, "Permission " + permission + " already granted");
            callback.onCallback(true);
        } else {
            Log.d(TAG, "Permission " + permission + " not yet granted");
            requestPermission(permission, message, denied_message, callback);
        }
    }

    private boolean isPermissionGranted(@NonNull String permission) {
        return ContextCompat.checkSelfPermission(this, "android.permission." + permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission,
                                   String message, String denied_message, @NonNull final SuccessCallback callback) {
            PermissionRationaleDialog.newInstance(false, permission, message, denied_message, callback)
                    .show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callback.onCallback(true);
        } else {
            PermissionDeniedDialog.newInstance(denied_message, false).show(getSupportFragmentManager(), "permission_denied");
            callback.onCallback(false);
        }
    }
}