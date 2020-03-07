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

public class SignInActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        authenticator = AuthenticatorFactory.getDependency();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppUser currentUser = authenticator.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
            startActivity(intent);
        }
    }

    public void toSignup(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

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
                            Intent intent = new Intent(getApplicationContext(), SignInSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            showErrorDialog();
                        }
                    }
                });
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle("Login failed")
                .setMessage("Please verify your credentials")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
