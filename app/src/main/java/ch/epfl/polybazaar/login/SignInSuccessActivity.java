package ch.epfl.polybazaar.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polybazaar.R;

public class SignInSuccessActivity extends AppCompatActivity {
    private Authenticator mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_success);

        mAuth = FirebaseAuthenticator.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AppUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && !currentUser.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
            startActivity(intent);
        }
    }

    public void signOut(View view) {
        mAuth.signOut();

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}
