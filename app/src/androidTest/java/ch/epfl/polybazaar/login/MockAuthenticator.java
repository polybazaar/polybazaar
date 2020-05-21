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

    public final static String TEST_USER_EMAIL = "test.user@epfl.ch";
    public final static String TEST_USER_NICKNAME = "testuser";
    public final static String TEST_USER_PASSWORD = "AGoodAndSafePassword1234";

    private Account currentUser;

    private HashMap<String, MockAccount> registeredUsers;

    private MockAuthenticator() {
        reset();
    }

    /**
     * Resets registered users
     */
    public void reset() {
        registeredUsers = new HashMap<>();
        MockAccount testUser = new MockAccount(TEST_USER_EMAIL, TEST_USER_NICKNAME, TEST_USER_PASSWORD);
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
    public Account getCurrentUser() {
        return currentUser;
    }

    @Override
    public Task<AuthenticatorResult> signIn(String email, String password) {
        MockAccount record = registeredUsers.get(email);
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
    public Task<AuthenticatorResult> createUser(String email, String nickname, String password) {
        if (MockPhoneSettings.getInstance().isAirPlaneModeEnabled()) {
            return Tasks.forException(new NetworkErrorException());
        }
        if (registeredUsers.get(email) == null) {
            MockAccount newUser = new MockAccount(email, nickname, password);
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
