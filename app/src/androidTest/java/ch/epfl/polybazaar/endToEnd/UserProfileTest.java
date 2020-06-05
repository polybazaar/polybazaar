package ch.epfl.polybazaar.endToEnd;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.google.android.gms.tasks.Tasks;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.filestorage.FileStoreFactory;
import ch.epfl.polybazaar.filestorage.LocalCache;
import ch.epfl.polybazaar.filestorage.MockFileStore;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.signInWithFromMainActivity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

public class UserProfileTest {

    private static final long SLEEP_TIME = 2000;
    private static Authenticator authenticator;
    boolean signedInFlag;
    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    useMockDataStore();
                    FileStoreFactory.setDependency(MockFileStore.getInstance());
                    LocalCache.setRoot("test-cache");
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    authenticator = AuthenticatorFactory.getDependency();
                    User testUser = new User("nickname", MockAuthenticator.TEST_USER_EMAIL);
                    testUser.save();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                    MockFileStore.getInstance().cleanUp();
                    LocalCache.cleanUp(InstrumentationRegistry.getInstrumentation().getContext());
                }
    };

    @Test
    public void ChangeProfilePictureTest() throws InterruptedException {
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_profile)).perform(click());
        Thread.sleep(SLEEP_TIME);
        onView(withId(R.id.saveProfileButton)).perform(scrollTo(), click());
        activityRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Test
    public void pressSignOutTest() {
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signOutButton)).perform(scrollTo(), click());
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signUpButton)).perform(click());
    }

    @Test
    public void testPasswordChangeWorks() throws InterruptedException {
        String newPassword = "MyNewAndSafePassword6969";
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_profile)).perform(click());

        onView(withId(R.id.newPassword)).perform(scrollTo());
        onView(withId(R.id.currentPassword)).perform(typeText(MockAuthenticator.TEST_USER_PASSWORD));


        closeSoftKeyboard();
        onView(withId(R.id.newPassword)).perform(scrollTo(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.confirmNewPassword)).perform(scrollTo(), typeText(newPassword));
        closeSoftKeyboard();
        onView(withId(R.id.savePassword)).perform(scrollTo(), click());
        signedInFlag = false;
        Tasks.whenAll(authenticator.signIn(MockAuthenticator.TEST_USER_EMAIL, newPassword).addOnSuccessListener(authenticatorResult -> signedInFlag = true)).
            addOnSuccessListener(aVoid -> assertThat(signedInFlag, is(true)));
    }

    @Test
    public void userHasNoOwnListings() {
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_profile)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.viewOwnListingsButton)).perform(scrollTo(), click());

        onView(withText(R.string.no_created_listings))
                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }


    private Activity currentActivity = null;

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
