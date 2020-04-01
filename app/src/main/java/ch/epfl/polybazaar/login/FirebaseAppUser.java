package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polybazaar.user.User;

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
    public Task<User> getUserData() {
        return User.fetch(fbUser.getEmail());
    }

    @Override
    public String getEmail() {
        return fbUser.getEmail();
    }

    @Override
    public String getNickname() {
        return fbUser.getDisplayName();
    }

    @Override
    public Task<Void> updatePassword(String newPassword) {
        return fbUser.updatePassword(newPassword);
    }


}
