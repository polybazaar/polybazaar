package ch.epfl.polybazaar.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polybazaar.R;

public class EmailVerificationActivity extends AppCompatActivity {
    private Authenticator mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuthenticator.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AppUser user = mAuth.getCurrentUser();

        if (user != null && user.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
            startActivity(intent);
        }
    }

    public void verify(View view) {
        AppUser user = mAuth.getCurrentUser();

        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        EmailVerificationActivity.this,
                                        "Verification e-mail sent", Toast.LENGTH_LONG
                                ).show();
                            } else {
                                Toast.makeText(
                                        EmailVerificationActivity.this,
                                        "Unable to send verification e-mail", Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    });
        }
    }

    public void reload(View view) {
        AppUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(
                                EmailVerificationActivity.this,
                                "Unable to reload", Toast.LENGTH_LONG
                        ).show();
                    }
                }
            });
        }
    }
}
