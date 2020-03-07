package ch.epfl.polybazaar.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.epfl.polybazaar.R;

public class SignUpActivity extends AppCompatActivity implements AuthenticatorDependent {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authenticator = AuthenticatorFactory.getDependency();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AppUser currentUser = authenticator.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
            startActivity(intent);
        }
    }

    public void submit(View view) {
        EditText emailView = findViewById(R.id.emailInput);
        EditText passwordView = findViewById(R.id.passwordInput);
        EditText confirmPasswordView = findViewById(R.id.confirmPasswordInput);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        if (!checkEmail(email)) {
            showErrorDialog(
                    "Invalid e-mail",
                    "Please enter a valid EPFL e-mail address"
            );
        } else if (!checkPassword(password)) {
            showErrorDialog(
                    "Invalid password",
                    "Please make sure that both passwords match"
            );
        } else {
            createUser(email, password);
        }
    }

    private void createUser(String email, String password) {
        authenticator.createUser(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthenticatorResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthenticatorResult> task) {
                        if (task.isSuccessful()) {
                            AppUser user = authenticator.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            showErrorDialog("Sign up failed", "Please try again");
                        }
                    }
                });
    }

    private boolean checkPassword(String password) {
        return true;
    }

    private boolean checkEmail(String email) {
        return true;
    }

    private void showErrorDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
}
