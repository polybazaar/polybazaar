package ch.epfl.polybazaar.widgets.permissions;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import ch.epfl.polybazaar.R;

/**
 * A dialog that displays a permission denied message.
 */
public class PermissionDeniedDialog extends DialogFragment {

    private static final String FINISH = "FINISH";
    private static final String MSG = "MSG";

    private boolean mFinishActivity = false;

    /**
     * Creates a new instance of this dialog and optionally finishes the calling Activity
     * when the 'Ok' button is clicked.
     */
    public static PermissionDeniedDialog newInstance(String message) {
        Bundle arguments = new Bundle();
        arguments.putString(MSG, message);
        PermissionDeniedDialog dialog = new PermissionDeniedDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(MSG))
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        } else return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
