package ch.epfl.polybazaar.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.widgets.MinimalAlertDialog;

import static ch.epfl.polybazaar.login.SignInActivity.backToMainWithSuccess;

public class EmailVerificationActivity extends AppCompatActivity {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        authenticator = AuthenticatorFactory.getDependency();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Account user = authenticator.getCurrentUser();
        /*
        if (user != null && !user.isEmailVerified()) {
            authenticator.signOut();
            // TODO delete the user
        }
         */
    }

    /**
     * Attempts to send a verification email to the user
     * @param view view that triggers the action
     */
    public void verify(View view) {
        Account user = authenticator.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                EmailVerificationActivity.this,
                                R.string.verification_email_sent, Toast.LENGTH_LONG
                        ).show();
                    } else {
                        MinimalAlertDialog.makeDialog(
                                EmailVerificationActivity.this,
                                R.string.verification_email_fail
                        );
                    }
                });
    }

    /**
     * Attempts to reload the user so that email verification status can be updated
     * @param view view that triggers the action
     */
    public void reload(View view) {
        Account user = authenticator.getCurrentUser();
        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (authenticator.getCurrentUser() != null) {
                    if (authenticator.getCurrentUser().isEmailVerified()) {
                        backToMainWithSuccess(this);
                    }
                }
            } else {
                MinimalAlertDialog.makeDialog(
                        EmailVerificationActivity.this,
                        R.string.reload_fail
                );
            }
        });
    }

    /**
     * Signs the user out of the app
     * @param view view that triggers the action
     */
    public void signOut(View view) {
        authenticator.signOut();
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}
