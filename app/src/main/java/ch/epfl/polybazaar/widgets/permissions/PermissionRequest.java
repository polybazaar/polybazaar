package ch.epfl.polybazaar.widgets.permissions;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * Useful for requesting permissions, at any time.
 * Usage guide:
 * 1. Create a new PermissionRequest, see PermissionRequest constructor
 * 2. Call assertPermission() on that PermissionRequest
 * 3. In the caller, which should be an AppCompatActivity, overwrite the following callback, as such:
 * @Override
 * public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 *     INSERT_YOUR_PermissionRequest_INSTANCE_HERE.onRequestPermissionsResult(requestCode, permissions, grantResults);
 * }
 * 4. The SuccessCallback given to the PermissionRequest returns true if the permission was successfully
 *    obtained or already granted and false if it failed to acquire the permission (i.e. the permission was denied).
 */
public final class PermissionRequest extends Fragment {

    private static final String TAG = "PermissionRequest";

    private String permission;
    private String message;
    private String denied_message;
    private SuccessCallback callback;
    private AppCompatActivity activity;

    /**
     * Creates a new permission request
     * @param activity the caller (i.e. this)
     * @param permission the permission you ask for, i.e : "ACCESS_FINE_LOCATION"
     * @param message message to display in order to explain why the permission is necessary, if null, no message is displayed,
     *                the message is shown only for the second request if the user has chosen deny & don't ask again on the first request
     * @param denied_message message to display if the permission is denied, if null, no message is displayed
     * @param callback a SuccessCallback implementation
     */
    public PermissionRequest(@NonNull AppCompatActivity activity, @NonNull String permission,
                             String message, String denied_message, @NonNull final SuccessCallback callback) {
        this.callback = callback;
        this.denied_message = denied_message;
        this.message = message;
        this.permission = "android.permission." + permission;
        this.activity = activity;
    }

    /**
     * Asks the necessary permission, if required.
     */
    public void assertPermission() {
        if (isPermissionGranted()) {
            Log.d(TAG, "Permission " + permission + " already granted");
            callback.onCallback(true);
        } else {
            Log.d(TAG, "Permission " + permission + " not yet granted");
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                requestPermission();
            } else {
                String[] permissions = {permission};
                ActivityCompat.requestPermissions(activity, permissions, 1);
            }
        }
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        PermissionRationaleDialog.newInstance(permission, message, denied_message, callback)
                    .show(activity.getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callback.onCallback(true);
        } else {
            if (denied_message != null) {
                PermissionDeniedDialog.newInstance(denied_message).show(activity.getSupportFragmentManager(), "permission_denied");
            }
            callback.onCallback(false);
        }
    }
}