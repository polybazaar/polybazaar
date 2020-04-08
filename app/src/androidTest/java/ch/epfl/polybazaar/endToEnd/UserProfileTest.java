package ch.epfl.polybazaar.endToEnd;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.database.callback.UserCallback;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.AuthenticatorResult;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static com.google.android.gms.tasks.Tasks.whenAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public class UserProfileTest {

    private static Authenticator authenticator;
    boolean signedInFlag;
    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    useMockDataStore();
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    authenticator = AuthenticatorFactory.getDependency();
                    User testUser = new User("nickname", MockAuthenticator.TEST_USER_EMAIL);
                    testUser.save();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
    };

    @Test
    public void testNameChangesWorks() throws InterruptedException {
        String newNickname = "new Nickname";
        String newFirstName = "Aurelien";
        String newLastName = "Queloz";
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.profileButton)).perform(click());
        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), clearText(), typeText(newNickname));
        closeSoftKeyboard();
        onView(withId(R.id.firstNameSelector)).perform(scrollTo(), clearText(), typeText(newFirstName));
        closeSoftKeyboard();
        onView(withId(R.id.lastNameSelector)).perform(scrollTo(), clearText(), typeText(newLastName));
        closeSoftKeyboard();
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());

        assertThat(authenticator.getCurrentUser().getNickname(), is(newNickname));
        User.fetch(MockAuthenticator.TEST_USER_EMAIL).addOnSuccessListener(user -> {
            assertThat(user.getNickName(), is(newNickname));
            assertThat(user.getFirstName(), is(newFirstName));
            assertThat(user.getLastName(), is(newLastName));
        });
    }

    @Test
    public void testPasswordChangeWorks() throws InterruptedException {
        String newPassword = "mynewpassword";
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.profileButton)).perform(click());
        Thread.sleep(100);
        onView(withId(R.id.currentPassword)).perform(scrollTo(), clearText(), typeText(MockAuthenticator.TEST_USER_PASSWORD));
        closeSoftKeyboard();
        onView(withId(R.id.newPassword)).perform(scrollTo(), clearText(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.confirmNewPassword)).perform(scrollTo(), clearText(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.savePassword)).perform(scrollTo(), click());
        signedInFlag = false;
        whenAll(authenticator.signIn(MockAuthenticator.TEST_USER_EMAIL, newPassword).addOnSuccessListener(authenticatorResult -> signedInFlag = true));
        assertThat(signedInFlag, is(true));
    }





    private void signInWithFromMainActivity(String email, String password){
        onView(withId(R.id.authenticationButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
}
}
