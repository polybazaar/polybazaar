package ch.epfl.polybazaar.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import ch.epfl.polybazaar.R;

public class AddImageDialog extends DialogFragment {

    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_add_image)
                .setPositiveButton(R.string.camera, (dialog, which) -> listener.onDialogPositiveClick(AddImageDialog.this))
                .setNegativeButton(R.string.library, (dialog, which) -> listener.onDialogNegativeClick(AddImageDialog.this));
        return builder.create();
    }

}
