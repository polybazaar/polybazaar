package ch.epfl.polybazaar.login;

import com.google.android.gms.tasks.Task;

public interface AppUser {
    boolean isEmailVerified();

    Task<Void> sendEmailVerification();

    Task<Void> reload();
}
