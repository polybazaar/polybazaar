package ch.epfl.polybazaar.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.SignInActivity;
import ch.epfl.polybazaar.login.SignUpActivity;

public class NotSignedIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_signed_in);
        findViewById(R.id.signInButton).setOnClickListener(v -> {
            Intent target = new Intent(this, SignInActivity.class);
            startActivity(target);
            finish();
        });
        findViewById(R.id.signUpButton).setOnClickListener(v -> {
            Intent target = new Intent(this, SignUpActivity.class);
            startActivity(target);
            finish();
        });

    }
}
