package ch.epfl.polybazaar.UI;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.utilities.InputValidity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static com.google.android.gms.tasks.Tasks.whenAll;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

public class UserProfileTest {

    public static final int SLEEP_TIME = 2000;
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

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), clearText(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        assertThat(onView(allOf(withId(R.id.nicknameInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), clearText(), typeText("UserNickname"), closeSoftKeyboard());
        onView(withId(R.id.firstNameSelector)).perform(scrollTo(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        assertThat(onView(allOf(withId(R.id.firstNameSelector), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));

        onView(withId(R.id.nicknameSelector)).perform(scrollTo(), clearText(), typeText("UserNickname"), closeSoftKeyboard());
        onView(withId(R.id.firstNameSelector)).perform(scrollTo(),clearText(),  typeText("Name"), closeSoftKeyboard());
        onView(withId(R.id.lastNameSelector)).perform(scrollTo(), clearText(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        assertThat(onView(allOf(withId(R.id.lastNameSelector), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
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
        assertThat(onView(allOf(withId(R.id.confirmNewPasswordInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
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
        assertThat(onView(allOf(withId(R.id.confirmNewPasswordInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
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
        Listing listing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "Multimedia");
        Task<Void> listingTask = listing.saveWithLiteVersion();
        Task<User> userTask = AuthenticatorFactory
                .getDependency()
                .getCurrentUser()
                .getUserData()
                .addOnSuccessListener(user -> {
                    user.addFavorite(listing.getId());
                });
        whenAll(listingTask, userTask);

        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        Intents.init();
        onView(withId(R.id.viewFavoritesButton)).perform(scrollTo(), click());
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void deletedFavoritesAreCleaned() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        Listing listing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "Multimedia");
        Task<User> userTask = AuthenticatorFactory
                .getDependency()
                .getCurrentUser()
                .getUserData()
                .addOnSuccessListener(user -> {
                    user.addFavorite(listing.getId());
                });
        whenAll(userTask);
        activityRule.launchActivity(new Intent());
        onView(withId(R.id.viewFavoritesButton)).perform(scrollTo(), click());
        onView(withText(R.string.no_favorites)).check(matches(isDisplayed()));
    }
}