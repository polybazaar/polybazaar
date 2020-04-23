package ch.epfl.polybazaar.widgets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.SignInActivity;
import ch.epfl.polybazaar.login.SignUpActivity;

public class NotSignedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_logged_in);
        findViewById(R.id.signInButton).setOnClickListener(v -> {
            // TODO: launch the correct activity
            Intent target = new Intent(this, SignInActivity.class);
            startActivity(target);
            finish();
        });
        findViewById(R.id.signUpButton).setOnClickListener(v -> {
            // TODO: launch the correct activity
            Intent target = new Intent(this, SignUpActivity.class);
            startActivity(target);
            finish();
        });
    }
}