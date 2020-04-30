package ch.epfl.polybazaar.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import ch.epfl.polybazaar.R;

public class PublishProfileDialog extends DialogFragment {

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
        builder.setMessage(R.string.publish_picture)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(PublishProfileDialog.this))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(PublishProfileDialog.this));
        return builder.create();
    }

}