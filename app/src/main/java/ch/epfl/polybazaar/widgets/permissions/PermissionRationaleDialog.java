package ch.epfl.polybazaar.widgets.permissions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private static String PERMISSION = "PERMISSION";
    private static String MSG = "MSG";
    private static String FINISH = "FINISH";
    private static SuccessCallback cb;

    private boolean mFinishActivity = false;



    public static PermissionRationaleDialog newInstance(boolean finishActivity, String permission,
                                                        String message, @NonNull final SuccessCallback callback) {
        Bundle arguments = new Bundle();
        arguments.putString(PERMISSION, permission);
        arguments.putString(MSG, message);
        arguments.putBoolean(FINISH, finishActivity);
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
                    String[] permission = {"android.permission." + arguments.getString(PERMISSION)};
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), permission,0);
                    // Do not finish the Activity while requesting permission.
                    mFinishActivity = false;
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    assert this.getFragmentManager() != null;
                    PermissionDeniedDialog.newInstance(false).show(this.getFragmentManager(), "permission_denied");
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cb.onCallback(true);
            } else {
                cb.onCallback(false);
            }
        }
    }

}