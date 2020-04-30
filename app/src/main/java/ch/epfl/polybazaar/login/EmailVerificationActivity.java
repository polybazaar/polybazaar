package ch.epfl.polybazaar.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.widgets.MinimalAlertDialog;

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
        Account user = authenticator.getCurrentUser();


        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
                    }
                });
    }

    /**
     * Attempts to reload the user so that email verification status can be updated
     * @param view view that triggers the action
     */
    public void reload(View view) {
        Account user = authenticator.getCurrentUser();

        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                    startActivity(intent);
                } else {
                    MinimalAlertDialog.makeDialog(
                            EmailVerificationActivity.this,
                            R.string.reload_fail
                    );
                }
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
