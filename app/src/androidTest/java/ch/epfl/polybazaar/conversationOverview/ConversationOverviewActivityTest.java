package ch.epfl.polybazaar.conversationOverview;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.AuthenticatorResult;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static com.google.android.gms.tasks.Tasks.whenAll;
import static java.util.UUID.randomUUID;

public class ConversationOverviewActivityTest {
    @Rule
    public final ActivityTestRule<ConversationOverviewActivity> conversationOverviewActivityRule =
            new ActivityTestRule<ConversationOverviewActivity>(ConversationOverviewActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                    useMockCategory();

                    Task<AuthenticatorResult> loginTask = AuthenticatorFactory.getDependency()
                            .signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
                    DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, MockAuthenticator.TEST_USER_EMAIL);
                    String newListingID = randomUUID().toString();
                    LiteListing liteListing = new LiteListing(newListingID, "Title", "0", "");
                    Task<Void> liteListingTask = liteListing.save();
                    ChatMessage message = new ChatMessage("user_email@epfl.ch", MockAuthenticator.TEST_USER_EMAIL, newListingID, "messageText", new Timestamp(new Date(System.currentTimeMillis())));
                    final String newMessageID = randomUUID().toString();
                    message.setId(newMessageID);
                    Task<Void> messageTask = message.save();

                    whenAll(liteListingTask, loginTask, messageTask);
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };

    @Test
    public void testConversationIsVisible() {
        onView(withId(R.id.title_conversation))
                .check(matches(withText("Title")));
    }

}