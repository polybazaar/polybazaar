package ch.epfl.polybazaar.widgets.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * A dialog that explains the use of the location permission and requests the necessary
 * permission.
 * <p>
 * The activity should implement
 * {@link androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback}
 * to handle permit or denial of this permission request.
 */
public class PermissionRationaleDialog extends DialogFragment {

    private static final String PERMISSION = "PERMISSION";
    private static final String MSG = "MSG";
    private static final String DENIED_MSG = "DENIED_MSG";
    private static final String FINISH = "FINISH";
    private static SuccessCallback cb;
    private static Activity activity;

    private boolean mFinishActivity = false;



    public static PermissionRationaleDialog newInstance(Activity caller, boolean finishActivity, String permission,
                                                        String message, String denied_message, @NonNull final SuccessCallback callback) {
        Bundle arguments = new Bundle();
        arguments.putString(PERMISSION, permission);
        arguments.putString(MSG, message);
        arguments.putString(DENIED_MSG, denied_message);
        arguments.putBoolean(FINISH, finishActivity);
        PermissionRationaleDialog dialog = new PermissionRationaleDialog();
        dialog.setArguments(arguments);
        cb = callback;
        activity = caller;
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
                    String[] permission = {"android.permission." + arguments.getString(PERMISSION)};
                    ActivityCompat.requestPermissions(activity, permission,0);
                    // Do not finish the Activity while requesting permission.
                    mFinishActivity = false;
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    assert this.getFragmentManager() != null;
                    PermissionDeniedDialog.newInstance(arguments.getString(DENIED_MSG), false).show(this.getFragmentManager(), "permission_denied");
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

}