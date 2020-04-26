package ch.epfl.polybazaar.widgets;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UserProfileActivity;
import ch.epfl.polybazaar.conversationOverview.ConversationOverview;
import ch.epfl.polybazaar.filllisting.FillListingActivity;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class bottomBarTest {

    int SLEEP_TIME = 2000;

    @Rule
    public final ActivityTestRule<NotSignedInActivity> activityRule =
            new ActivityTestRule<>(
                    NotSignedInActivity.class,
                    true,
                    true);


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
        intended(hasComponent(FillListingActivity.class.getName()));
        Intents.release();
    }
    @Test
    public void messagesGoToConversations() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_messages).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(ConversationOverview.class.getName()));
        Intents.release();
    }
    @Test
    public void profileGoToProfile() throws Throwable {
        Intents.init();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.action_profile).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(UserProfileActivity.class.getName()));
        Intents.release();
    }
}
