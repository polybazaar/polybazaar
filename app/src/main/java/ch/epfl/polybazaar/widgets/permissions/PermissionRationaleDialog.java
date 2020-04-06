package ch.epfl.polybazaar.widgets.permissions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

/**
 * A dialog that explains the use of the permission and requests the necessary
 * permission.
 */
public class PermissionRationaleDialog extends DialogFragment {

    private static final String PERMISSION = "PERMISSION";
    private static final String MSG = "MSG";
    private static final String DENIED_MSG = "DENIED_MSG";
    private static SuccessCallback cb;


    /**
     * Create a new Rationale Dialog to request the permission
     * @param permission the permission in question
     * @param message the explanation message of why the permission is needed
     * @param denied_message what to tell the user when he/she denies the permission
     * @param callback a SuccessCallback implementation
     * @return the dialog
     */
    public static PermissionRationaleDialog newInstance(String permission,
                                                 String message, String denied_message, @NonNull final SuccessCallback callback) {
        Bundle arguments = new Bundle();
        arguments.putString(PERMISSION, permission);
        arguments.putString(MSG, message);
        arguments.putString(DENIED_MSG, denied_message);
        PermissionRationaleDialog dialog = new PermissionRationaleDialog();
        dialog.setArguments(arguments);
        cb = callback;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null && getActivity() != null && arguments.getString(PERMISSION)!= null) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(arguments.getString(MSG))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        // Open App Settings
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        boolean isIntentSafe = activities.size() > 0;
                        if (isIntentSafe) {
                            startActivity(intent);
                        }
                        cb.onCallback(false);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        assert this.getFragmentManager() != null;
                        if (arguments.getString(DENIED_MSG) != null) {
                            PermissionDeniedDialog.newInstance(arguments.getString(DENIED_MSG))
                                    .show(this.getFragmentManager(), "permission_denied");
                        }
                        cb.onCallback(false);
                    })
                    .create();
        } else {
            return new AlertDialog.Builder(getActivity()).setMessage("ERROR")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        cb.onCallback(false);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        cb.onCallback(false);
                    }).create();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}