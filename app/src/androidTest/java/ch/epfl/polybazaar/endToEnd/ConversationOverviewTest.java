package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;
import ch.epfl.polybazaar.testingUtilities.SignInUtilities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;

public class ConversationOverviewTest {

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                    useMockCategory();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };

    //@Test
    public void testMessagesAreDisplayedInChat() throws InterruptedException {
        String otherUserEmail = "otherother.user@epfl.ch";
        String title = "Send chat";
        String message = "Hello how are you?";
        String message2 = "Fine and you?";
        String id = "MyFakeID";
        SignInUtilities.signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        DatabaseStoreUtilities.storeNewMessage(otherUserEmail, MockAuthenticator.TEST_USER_EMAIL, id, message);
        DatabaseStoreUtilities.storeNewMessage(MockAuthenticator.TEST_USER_EMAIL, otherUserEmail, id, message2);
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText(otherUserEmail)).perform(scrollTo(), click());
        onView(withText(message)).check(matches(isDisplayed()));
        onView(withText(message2)).check(matches(isDisplayed()));
    }
}
