package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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
    public Task<Void> updateNickname(String newNickname) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newNickname).build();
        return fbUser.updateProfile(profileUpdates);
    }

    @Override
    public Task<Void> updatePassword(String newPassword) {
        return fbUser.updatePassword(newPassword);
    }


}
