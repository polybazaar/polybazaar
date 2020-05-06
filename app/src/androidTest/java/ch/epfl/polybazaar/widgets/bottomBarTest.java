package ch.epfl.polybazaar.widgets;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.UserProfile;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewActivity;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;

public class bottomBarTest {

    int SLEEP_TIME = 2000;

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule = new ActivityTestRule<SalesOverview>(SalesOverview.class) {
        @Override
        protected void beforeActivityLaunched() {
            useMockCategory();
            useMockDataStore();
            AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
            AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL,MockAuthenticator.TEST_USER_PASSWORD);
            DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, MockAuthenticator.TEST_USER_EMAIL);
        }

        @Override
        protected void afterActivityFinished() {
            MockAuthenticator.getInstance().reset();
        }
    };


    @Test
    public void homeGoHome() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_home).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }
    @Test
    public void addGoToAddItem() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_add_item).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(FillListing.class.getName()));
        Intents.release();
    }
    @Test
    public void messagesGoToConversations() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_messages).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(ConversationOverviewActivity.class.getName()));
        Intents.release();
    }
    @Test
    public void profileGoToProfile() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_profile).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(UserProfile.class.getName()));
        Intents.release();
    }
}
