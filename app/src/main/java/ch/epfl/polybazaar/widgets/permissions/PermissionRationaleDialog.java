package ch.epfl.polybazaar.widgets.permissions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * A dialog that explains the use of the permission and requests the necessary
 * permission.
 */
public class PermissionRationaleDialog extends DialogFragment {

    private static final String PERMISSION = "PERMISSION";
    private static final String MSG = "MSG";
    private static final String DENIED_MSG = "DENIED_MSG";
    private static final String FINISH = "FINISH";
    private static final String TAG = "PermissionRD";
    private static SuccessCallback cb;

    private boolean mFinishActivity = false;


    /**
     * Create a new Rationale Dialog to request the permission
     * @param finish if we should end the parent activity when the permission request is over
     * @param permission the permission in question
     * @param message the explanation message of why the permission is needed
     * @param denied_message what to tell the user when he/she denies the permission
     * @param callback a SuccessCallback implementation
     * @return the dialog
     */
    public static PermissionRationaleDialog newInstance(boolean finish, String permission,
                                                 String message, String denied_message, @NonNull final SuccessCallback callback) {
        Bundle arguments = new Bundle();
        arguments.putString(PERMISSION, permission);
        arguments.putString(MSG, message);
        arguments.putString(DENIED_MSG, denied_message);
        arguments.putBoolean(FINISH, finish);
        PermissionRationaleDialog dialog = new PermissionRationaleDialog();
        dialog.setArguments(arguments);
        cb = callback;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        assert arguments != null;
        mFinishActivity = arguments.getBoolean(FINISH);
        return new AlertDialog.Builder(getActivity())
                .setMessage(arguments.getString(MSG))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // After click on Ok, request the permission.
                    String[] permissions = {"android.permission." + arguments.getString(PERMISSION)};
                    requestPermissions(permissions, 1);
                    // Do not finish the Activity while requesting permission.
                    mFinishActivity = false;
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    assert this.getFragmentManager() != null;
                    PermissionDeniedDialog.newInstance(arguments.getString(DENIED_MSG), false)
                            .show(this.getFragmentManager(), "permission_denied");
                    cb.onCallback(false);
                })
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mFinishActivity) {
            Toast.makeText(getActivity(),
                    R.string.permission_required_toast,
                    Toast.LENGTH_SHORT)
                    .show();
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission granted");
            cb.onCallback(true);
        } else {
            Log.d(TAG, "permission denied");
            assert this.getFragmentManager() != null;
            assert getArguments() != null;
            PermissionDeniedDialog.newInstance(getArguments().getString(DENIED_MSG), false)
                    .show(this.getFragmentManager(), "permission_denied");
            cb.onCallback(false);
        }
    }

     */

}