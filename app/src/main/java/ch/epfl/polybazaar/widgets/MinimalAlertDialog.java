package ch.epfl.polybazaar.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;

import ch.epfl.polybazaar.R;

/**
 * Very basic dismissible alert dialog
 */
public class MinimalAlertDialog {
    /**
     * Displays a basic alert dialog
     * @param ctx context
     * @param msg message to display
     */
    public static void makeDialog(Context ctx, @StringRes int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(msg)
                .setPositiveButton(R.string.alert_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
