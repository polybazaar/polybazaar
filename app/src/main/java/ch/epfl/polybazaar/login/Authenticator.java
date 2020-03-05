package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface Authenticator {
    AppUser getCurrentUser();

    Task<AuthResult> signIn(String email, String password);

    Task<AuthResult> createUser(String email, String password);

    void signOut();
}
