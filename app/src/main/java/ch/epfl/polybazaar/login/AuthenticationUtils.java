package ch.epfl.polybazaar.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.NotSignedIn;
import ch.epfl.polybazaar.widgets.MinimalAlertDialog;

public final class AuthenticationUtils {
    // class is non-instantiable
    private AuthenticationUtils() {}

    public static boolean checkAccessAuthorization(Context ctx) {
        Authenticator authenticator = AuthenticatorFactory.getDependency();
        Account user = authenticator.getCurrentUser();
        Intent intent;
        if (user == null) {
            intent = new Intent(ctx, NotSignedIn.class);
            ctx.startActivity(intent);
            return false;
        } else if (!user.isEmailVerified()) {
            intent = new Intent(ctx, EmailVerificationActivity.class);
            ctx.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

    public static void sendVerificationEmailWithResponse(Context ctx) {
        Authenticator authenticator = AuthenticatorFactory.getDependency();
        Account user = authenticator.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ctx,R.string.verification_email_sent, Toast.LENGTH_LONG).show();
                    } else {
                        MinimalAlertDialog.makeDialog(ctx, R.string.verification_email_fail);
                    }
                });
    }
}
