package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.bottomBar;
import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class SignInActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        authenticator = AuthenticatorFactory.getDependency();

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), SignInActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Account currentUser = authenticator.getCurrentUser();

        if (currentUser != null) {
            currentUser.getUserData().addOnSuccessListener(new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    updateToken(FirebaseInstanceId.getInstance().getToken(), user.getEmail());
                    Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                    startActivity(intent);
                }
            });

        }
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthenticatorResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthenticatorResult> task) {
                        if (task.isSuccessful()) {
                            updateToken(FirebaseInstanceId.getInstance().getToken(), email);
                            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            makeDialog(SignInActivity.this, R.string.verify_credentials);
                        }
                    }
                });
    }

    private void updateToken(String token, String email){
        User.updateField("token", email, token);
    }
}

