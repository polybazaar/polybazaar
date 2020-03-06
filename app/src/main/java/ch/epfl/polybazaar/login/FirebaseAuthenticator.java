package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    public Task<AuthenticatorResult> signIn(String email, String password) {
        Task<AuthResult> task = fbAuth.signInWithEmailAndPassword(email, password);
        return task.onSuccessTask((t) -> Tasks.call(FirebaseAuthenticatorResult::new));


    }

    @Override
    public Task<AuthenticatorResult> createUser(String email, String password) {
        Task<AuthResult> task = fbAuth.createUserWithEmailAndPassword(email, password);
        return task.onSuccessTask((t) -> Tasks.call(FirebaseAuthenticatorResult::new));
    }

    @Override
    public void signOut() {
        fbAuth.signOut();
    }
}
