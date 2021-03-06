package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

    /**
     * Attempts to send a verification email to the user
     * @param view view that triggers the action
     */
    public void verify(View view) {
        AuthenticationUtils.sendVerificationEmailWithResponse(this);
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
