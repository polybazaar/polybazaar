package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.user.UserDatabase;

/**
 * Adapter for firebase authentication
 */
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
    public Task<AuthenticatorResult> createUser(String email, String nickname, String password) {
        Task<AuthResult> task = fbAuth.createUserWithEmailAndPassword(email, password);

        // Once the user is created, we immediately change its display name (nickname)
        Task<AuthenticatorResult> setProfileTask = task.onSuccessTask((creationRes) -> {
                    FirebaseUser user = creationRes.getUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build();

                    return user.updateProfile(profileUpdates).onSuccessTask(
                            (updateRes) -> Tasks.forResult(new FirebaseAuthenticatorResult())
                    );
        });

        // TODO check that all actions complete successfully (should be easier with tasks)
        User user = new User(nickname, email);
        UserDatabase.storeNewUser(user, result -> {});

        return setProfileTask;
    }

    @Override
    public void signOut() {
        fbAuth.signOut();
    }
}
