package ch.epfl.polybazaar.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.epfl.polybazaar.R;

public class SignInSuccessActivity extends AppCompatActivity {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_success);

        authenticator = AuthenticatorFactory.getDependency();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AppUser currentUser = authenticator.getCurrentUser();

        if (!currentUser.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
            startActivity(intent);
        }
    }

    public void signOut(View view) {
        authenticator.signOut();

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}
