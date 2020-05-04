package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.widget.Button;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UtilitiesTest;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;
import ch.epfl.polybazaar.testingUtilities.SignInUtilities;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.getDependency;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class UserProfileTest {

    @Rule
    public final ActivityTestRule<UserProfile> activityRule =
            new ActivityTestRule<>(
                    UserProfile.class,
                    true,
                    false);

    @Before
    public void init() {
        useMockDataStore();
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
    }

    @After
    public void cleanup() {
        MockAuthenticator.getInstance().reset();
    }

    @Test
    public void testUserProfileWithoutSignIn() {
        Intent intent = new Intent();

        Intents.init();
        activityRule.launchActivity(intent);
        intended(hasComponent(NotSignedIn.class.getName()));
        Intents.release();
    }

    @Test
    public void testInvalidChanges() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        onView(withText(R.string.signup_nickname_invalid)).check(matches(isDisplayed()));
        onView(withText("Back")).perform(scrollTo(), click());

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), typeText("UserNickname"), closeSoftKeyboard());
        onView(withId(R.id.firstNameSelector)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        onView(withText(R.string.invalid_first_name)).check(matches(isDisplayed()));
        onView(withText("Back")).perform(scrollTo(), click());

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), typeText("UserNickname"), closeSoftKeyboard());
        onView(withId(R.id.firstNameSelector)).perform(scrollTo(), typeText("Name"), closeSoftKeyboard());
        onView(withId(R.id.lastNameSelector)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        onView(withText(R.string.invalid_last_name)).check(matches(isDisplayed()));
        onView(withText("Back")).perform(scrollTo(), click());
    }

    @Test
    public void testPasswordDontMatch() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.currentPassword)).perform(scrollTo(), typeText(MockAuthenticator.TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.newPassword)).perform(scrollTo(), typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.confirmNewPassword)).perform(scrollTo(), typeText("12345"), closeSoftKeyboard());
        onView(withId(R.id.savePassword)).perform(scrollTo(), click());
        onView(withText(R.string.signup_passwords_not_matching)).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordNotValid() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.currentPassword)).perform(scrollTo(), typeText(MockAuthenticator.TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.newPassword)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.confirmNewPassword)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.savePassword)).perform(scrollTo(), click());
        onView(withText(R.string.signup_passwords_weak)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewFavoritesWhenEmpty() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.viewFavoritesButton)).perform(scrollTo(), click());
        onView(withText(R.string.no_favorites)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewFavorites() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Listing listing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "");
        Tasks.await(listing.save());
        Tasks.await(AuthenticatorFactory
                .getDependency()
                .getCurrentUser()
                .getUserData()
                .addOnSuccessListener(user -> {
                    user.addFavorite(listing);
                }));

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        Intents.init();
        onView(withId(R.id.viewFavoritesButton)).perform(scrollTo(), click());
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    /*@Test
    public void testChangeImage() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Intent intent = new Intent();
        activityRule.launchActivity(intent);


    }*/

}