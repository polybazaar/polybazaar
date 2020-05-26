package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.utilities.InputValidity;

import static ch.epfl.polybazaar.utilities.InputValidity.emailIsValid;
import static ch.epfl.polybazaar.utilities.InputValidity.nicknameValidity;
import static ch.epfl.polybazaar.utilities.InputValidity.passwordValidity;
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

        TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);
        TextInputLayout nicknameInputLayout = findViewById(R.id.nicknameInputLayout);
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);
        TextInputLayout confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);

        String email = emailView.getText().toString();
        String nickname = nicknameView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        boolean allValid = true;

        //Below, tags are set for testing purpose as there is no easy way to check the error of an Input Layout from android.material
        if (!emailIsValid(email)) {
            emailInputLayout.setError(getString(R.string.signup_email_invalid));
            emailInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            emailInputLayout.setError(null);
        }

        if (!nicknameValidity(nickname, getApplicationContext()).equals("")) {
            nicknameInputLayout.setError(nicknameValidity(nickname, getApplicationContext()));
            nicknameInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            nicknameInputLayout.setError(null);
        }

        if (!passwordValidity(password, getApplicationContext()).equals("")) {
            passwordInputLayout.setError(passwordValidity(password, getApplicationContext()));
            passwordInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            passwordInputLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError(getString(R.string.signup_passwords_not_matching));
            confirmPasswordInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            confirmPasswordInputLayout.setError(null);
        }

        if(allValid) {
            createUser(email, nickname, password);
        }
    }

    private void createUser(String email, String nickname, String password) {
        authenticator.createUser(email, nickname, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        AuthenticationUtils.sendVerificationEmailWithResponse(this);
                        Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
                        startActivity(intent);
                    } else {
                        makeDialog(SignUpActivity.this, R.string.signup_error);
                    }
                });
    }


}
