package ch.epfl.polybazaar.widgets;

import android.app.Activity;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UserProfileActivity;
import ch.epfl.polybazaar.conversationOverview.ConversationOverview;
import ch.epfl.polybazaar.filllisting.FillListingActivity;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_EMAIL;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_PASSWORD;

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
