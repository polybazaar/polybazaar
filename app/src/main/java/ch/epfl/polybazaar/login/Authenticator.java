package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

public interface Authenticator {
    AppUser getCurrentUser();

    Task<AuthenticatorResult> signIn(String email, String password);

    Task<AuthenticatorResult> createUser(String email, String password);

    void signOut();
}
