package ch.epfl.polybazaar.login;

import android.accounts.NetworkErrorException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import ch.epfl.polybazaar.user.User;

/**
 * Mocked app user
 */
public class MockAccount implements Account {
    private boolean sentEmail;
    private boolean isVerified;
    private String email;
    private String nickname;
    private String password;
    private User dbUser;

    public MockAccount(String email, String nickname, String password) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.dbUser = new User(nickname, email);
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

    @Override
    public Task<User> getUserData() {
        return Tasks.forResult(dbUser);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Task<Void> updateNickname(String newNickname) {
        nickname = newNickname;
        return Tasks.forCanceled();
    }

    @Override
    public Task<Void> updatePassword(String newPassword) {
        password = newPassword;
        return Tasks.forCanceled();
    }
}
