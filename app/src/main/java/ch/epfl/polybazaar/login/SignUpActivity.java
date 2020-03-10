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

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.*;

public class SignUpActivity extends AppCompatActivity {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authenticator = AuthenticatorFactory.getDependency();
    }

    public void submit(View view) {
        EditText emailView = findViewById(R.id.emailInput);
        EditText passwordView = findViewById(R.id.passwordInput);
        EditText confirmPasswordView = findViewById(R.id.confirmPasswordInput);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        if (!checkEmail(email)) {
            makeDialog(SignUpActivity.this, R.string.signup_email_invalid);
        } else if (!password.equals(confirmPassword)) {
            makeDialog(SignUpActivity.this, R.string.signup_passwords_not_matching);
        } else if (!checkPassword(password)) {
            makeDialog(SignUpActivity.this, R.string.signup_passwords_weak);
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
                            makeDialog(SignUpActivity.this, R.string.signup_error);
                        }
                    }
                });
    }

    private boolean checkPassword(String password) {
        return password.length() >= 6;
    }

    private boolean checkEmail(String email) {
        return email.endsWith("@epfl.ch");
    }
}
