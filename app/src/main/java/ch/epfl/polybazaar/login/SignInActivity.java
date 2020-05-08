package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class SignInActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        authenticator = AuthenticatorFactory.getDependency();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Account currentUser = authenticator.getCurrentUser();
        if (currentUser != null) {
            currentUser.getUserData().addOnSuccessListener(user -> {
                backToMainWithSuccess(this);
            });
        }
    }

    public static void backToMainWithSuccess(AppCompatActivity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), SalesOverview.class);
        activity.startActivity(intent);
        Toast.makeText(activity, R.string.sign_in_success, Toast.LENGTH_LONG).show();
    }

    /**
     * Move to sign up activity
     * @param view view that triggers the action
     */
    public void toSignup(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to authenticate the user with the given credentials
     * @param view view that triggers the action
     */
    public void submit(View view) {
        EditText emailView = findViewById(R.id.emailInput);
        EditText passwordView = findViewById(R.id.passwordInput);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        authenticator.signIn(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (authenticator.getCurrentUser() != null) {
                            if (!authenticator.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
                                startActivity(intent);
                            } else {
                                backToMainWithSuccess(this);
                            }
                        }
                    } else {
                        makeDialog(SignInActivity.this, R.string.verify_credentials);
                    }
                });
    }
}

