package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
}
