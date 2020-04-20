package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.Utilities;

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class SignUpActivity extends AppCompatActivity {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authenticator = AuthenticatorFactory.getDependency();
    }

    /**
     * Attempts to create an account with the given credentials
     * @param view view that triggers the action
     */
    public void submit(View view) {
        EditText emailView = findViewById(R.id.emailInput);
        EditText passwordView = findViewById(R.id.passwordInput);
        EditText confirmPasswordView = findViewById(R.id.confirmPasswordInput);
        EditText nicknameView = findViewById(R.id.nicknameInput);

        String email = emailView.getText().toString();
        String nickname = nicknameView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        if (!Utilities.emailIsValid(email)) {
            makeDialog(SignUpActivity.this, R.string.signup_email_invalid);
        } else if (!Utilities.nickNameIsValid(nickname)) {
            makeDialog(SignUpActivity.this, R.string.signup_nickname_invalid);
        } else if (!password.equals(confirmPassword)) {
            makeDialog(SignUpActivity.this, R.string.signup_passwords_not_matching);
        } else if (!Utilities.passwordIsValid(password)) {
            makeDialog(SignUpActivity.this, R.string.signup_passwords_weak);
        } else {
            createUser(email, nickname, password);
        }
    }

    private void createUser(String email, String nickname, String password) {
        authenticator.createUser(email, nickname, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthenticatorResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthenticatorResult> task) {
                        if (task.isSuccessful()) {
                            Account user = authenticator.getCurrentUser();

                            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            makeDialog(SignUpActivity.this, R.string.signup_error);
                        }
                    }
                });
    }


}
