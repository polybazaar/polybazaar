package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticator implements Authenticator {
    private static FirebaseAuthenticator INSTANCE;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    private FirebaseAuthenticator() {}

    public static FirebaseAuthenticator getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseAuthenticator();
        return INSTANCE;
    }

    @Override
    public AppUser getCurrentUser() {
        FirebaseUser fbUser = fbAuth.getCurrentUser();
        return fbUser == null ? null : new FirebaseAppUser(fbAuth.getCurrentUser());
    }

    @Override
    public Task<AuthResult> signIn(String email, String password) {
        return fbAuth.signInWithEmailAndPassword(email, password);
    }

    @Override
    public Task<AuthResult> createUser(String email, String password) {
        return fbAuth.createUserWithEmailAndPassword(email, password);
    }

    @Override
    public void signOut() {
        fbAuth.signOut();
    }
}
