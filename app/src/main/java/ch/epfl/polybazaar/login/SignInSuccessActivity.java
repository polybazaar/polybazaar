package ch.epfl.polybazaar.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.bottomBar;
import ch.epfl.polybazaar.user.User;

public class SignInSuccessActivity extends AppCompatActivity {
    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_success);

        authenticator = AuthenticatorFactory.getDependency();

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(),SignInSuccessActivity.this));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Account currentUser = authenticator.getCurrentUser();

        if (currentUser != null && !currentUser.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), EmailVerificationActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Signs the user out of the app
     * @param view view that triggers the action
     */
    public void signOut(View view) {
        authenticator.signOut();

        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }

    /**
     * Change activity to main activity
     * @param view view that triggers the action
     */
    public void toMain(View view) {
        Intent intent = new Intent(getApplicationContext(), SalesOverview.class);
        startActivity(intent);
    }
}
