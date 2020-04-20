package ch.epfl.polybazaar.endToEnd;

import android.app.Activity;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collection;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    /*
    @Test
    public void testPasswordChangeWorks() throws InterruptedException {
        String newPassword = "mynewpassword";
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.profileButton)).perform(click());

        onView(withId(R.id.newPassword)).perform(scrollTo());
        onView(withId(R.id.currentPassword)).perform(typeText(MockAuthenticator.TEST_USER_PASSWORD));


        closeSoftKeyboard();
        onView(withId(R.id.newPassword)).perform(scrollTo(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.confirmNewPassword)).perform(scrollTo(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.savePassword)).perform(scrollTo(), click());
        signedInFlag = false;
        Tasks.whenAll(authenticator.signIn(MockAuthenticator.TEST_USER_EMAIL, newPassword).addOnSuccessListener(authenticatorResult -> signedInFlag = true)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                assertThat(signedInFlag, is(true));
            }
        });
    }
     */

    /*@Test
    public void userHasNoOwnListings() {
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.profileButton)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.viewOwnListingsButton)).perform(click());

        onView(withText(R.string.no_created_listings))
                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }*/

    /*@Test
    public void userHasOwnListings() {
        authenticator.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        AppUser authAccount = authenticator.getCurrentUser();
        authAccount.getUserData().addOnSuccessListener(user -> {
            if (user != null) {
                user.addOwnListing("listing_1");
            }
        });
        authenticator.signOut();
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.profileButton)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.viewOwnListingsButton)).perform(click());
        Activity activity = getActivityInstance();
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<String> ownListings = bundle.getStringArrayList(bundleKey);
        assertEquals("listing_1", ownListings.get(0));
    }*/

    private void signInWithFromMainActivity(String email, String password){
        onView(withId(R.id.authenticationButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
}


    Activity currentActivity = null;

    public Activity getActivityInstance(){
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity)resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }
}
