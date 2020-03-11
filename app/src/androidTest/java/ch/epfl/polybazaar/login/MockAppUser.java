package ch.epfl.polybazaar.login;

import android.accounts.NetworkErrorException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

/**
 * Mocked app user
 */
public class MockAppUser implements AppUser {
    private boolean sentEmail;
    private boolean isVerified;
    private String email;
    private String password;

    public MockAppUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Verify that the specified passwords matches the users's password
     * @param password password
     * @return true iff the password matches
     */
    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    @Override
    public boolean isEmailVerified() {
        return isVerified;
    }

    @Override
    public Task<Void> sendEmailVerification() {
        if (MockPhoneSettings.getInstance().isAirPlaneModeEnabled()) {
            return Tasks.forException(new NetworkErrorException());
        } else {
            return Tasks.call(() -> {
                sentEmail = true;
                return null;
            });
        }
    }

    @Override
    public Task<Void> reload() {
        if (MockPhoneSettings.getInstance().isAirPlaneModeEnabled()) {
            return Tasks.forException((new NetworkErrorException()));
        } else {
            return Tasks.call(() -> {
                isVerified = sentEmail;
                return null;
            });
        }

    }
}
