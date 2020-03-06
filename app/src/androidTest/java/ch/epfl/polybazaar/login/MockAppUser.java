package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class MockAppUser implements AppUser {
    private boolean sentEmail;
    private boolean isVerified;
    private String email;
    private String password;

    public MockAppUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    @Override
    public boolean isEmailVerified() {
        return isVerified;
    }

    @Override
    public Task<Void> sendEmailVerification() {
        return Tasks.call(() -> {
            sentEmail = true;
            return null;
        });
    }

    @Override
    public Task<Void> reload() {
        return Tasks.call(() -> {
            isVerified = sentEmail;
            return null;
        });
    }
}
