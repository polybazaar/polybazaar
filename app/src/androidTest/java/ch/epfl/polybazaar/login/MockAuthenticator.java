package ch.epfl.polybazaar.login;

import android.accounts.NetworkErrorException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;

/**
 * Singleton class that can be used to mock an authentication API
 */
public class MockAuthenticator implements Authenticator {
    private static MockAuthenticator INSTANCE;

    public final static String TEST_USER_EMAIL = "testuser@epfl.ch";
    public final static String TEST_USER_PASSWORD = "abcdef";

    private AppUser currentUser;

    private HashMap<String, MockAppUser> registeredUsers;

    private MockAuthenticator() {
        reset();
    }

    /**
     * Resets registered users
     */
    public void reset() {
        registeredUsers = new HashMap<>();
        MockAppUser testUser = new MockAppUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        testUser.sendEmailVerification();
        testUser.reload();
        registeredUsers.put(TEST_USER_EMAIL, testUser);
        currentUser = null;
    }

    /**
     * Returns the singleton instance
     * @return instance
     */
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
        if (MockPhoneSettings.getInstance().isAirPlaneModeEnabled()) {
            return Tasks.forException(new NetworkErrorException());
        } else if (record != null && record.checkPassword(password)) {
            currentUser = record;
            return Tasks.call(MockAuthenticatorResult::new);
        } else {
            return Tasks.forException(new NullPointerException());
        }
    }

    @Override
    public Task<AuthenticatorResult> createUser(String email, String password) {
        if (MockPhoneSettings.getInstance().isAirPlaneModeEnabled()) {
            return Tasks.forException(new NetworkErrorException());
        }
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
