package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class MockAuthenticator implements Authenticator {
    private static MockAuthenticator INSTANCE;

    private AppUser currentUser;

    private HashMap<String, MockAppUser> registeredUsers;

    private MockAuthenticator() {
        reset();
    }

    public void reset() {
        registeredUsers = new HashMap<>();
        currentUser = null;
    }

    public static MockAuthenticator getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MockAuthenticator();
        return INSTANCE;
    }

    @Override
    public AppUser getCurrentUser() {
        return currentUser;
    }

    @Override
    public Task<AuthenticatorResult> signIn(String email, String password) {
        MockAppUser record = registeredUsers.get(email);
        if (record != null && record.checkPassword(password)) {
            currentUser = record;
            return Tasks.call(MockAuthenticatorResult::new);
        } else {
            return Tasks.forException(new NullPointerException());
        }
    }

    @Override
    public Task<AuthenticatorResult> createUser(String email, String password) {
        if (registeredUsers.get(email) == null) {
            MockAppUser newUser = new MockAppUser(email, password);
            registeredUsers.put(email, newUser);
            currentUser = newUser;
            return Tasks.call(MockAuthenticatorResult::new);
        } else {
            return Tasks.forException(new NullPointerException());
        }
    }

    @Override
    public void signOut() {
        currentUser = null;
    }
}
