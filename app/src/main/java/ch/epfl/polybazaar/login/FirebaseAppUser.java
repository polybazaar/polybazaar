package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polybazaar.database.callback.UserCallback;

import static ch.epfl.polybazaar.user.UserDatabase.fetchUser;

/**
 * Adapter for firebase app user
 */
public class FirebaseAppUser implements AppUser {
    private FirebaseUser fbUser;

    public FirebaseAppUser(FirebaseUser user) {
        fbUser = user;
    }

    @Override
    public boolean isEmailVerified() {
        return fbUser.isEmailVerified();
    }

    @Override
    public Task<Void> sendEmailVerification() {
        return fbUser.sendEmailVerification();
    }

    @Override
    public Task<Void> reload() {
        return fbUser.reload();
    }

    @Override
    public void getUserData(UserCallback cb) {
        fetchUser(fbUser.getEmail(), cb);
    }

    @Override
    public String getEmail() {
        return fbUser.getEmail();
    }

    @Override
    public String getNickname() {
        return fbUser.getDisplayName();
    }
}
