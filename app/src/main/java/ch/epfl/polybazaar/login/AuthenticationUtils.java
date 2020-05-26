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

    /**
     * Checks that the user is authorized to access a resource restricted to authenticated users.
     * If the user is not authenticated, redirects to login activity. If the user is authenticated
     * but has not verified the email yet, redirects to email verification activity.
     * @param ctx app context
     * @return true if the user is signed in and has verified the email, false otherwise
     */
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

    /**
     * Sends a verification e-mail to the authenticated user if possible and displays an alert message.
     * @param ctx app context
     */
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
