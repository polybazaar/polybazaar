package ch.epfl.polybazaar.chat;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static java.util.UUID.randomUUID;

public class ChatActivityTest {
    String listingID;
    String otherUserEmail = "another.user@epfl.ch";

    @Rule
    public final ActivityTestRule<ChatActivity> chatActivityRule =
            new ActivityTestRule<>(
                    ChatActivity.class,
                    true,
                    false);

    @Before
    public void init() throws ExecutionException, InterruptedException {
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        useMockDataStore();
        useMockCategory();

        Tasks.await(AuthenticatorFactory.getDependency().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        listingID = randomUUID().toString();
        DatabaseStoreUtilities.storeNewListing("Title", MockAuthenticator.TEST_USER_EMAIL, listingID);
        DatabaseStoreUtilities.storeNewMessage(otherUserEmail, MockAuthenticator.TEST_USER_EMAIL, listingID, "Hello!");
        DatabaseStoreUtilities.storeNewMessage( MockAuthenticator.TEST_USER_EMAIL, otherUserEmail, listingID, "Hi!");
        DatabaseStoreUtilities.storeNewUser("otherNickname", otherUserEmail);
        DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, MockAuthenticator.TEST_USER_EMAIL);
    }

    @After
    public void cleanup() {
        MockAuthenticator.getInstance().reset();
    }

    @Test
    public void testChatMessagesAppear() {
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.bundleListingId,listingID);
        intent.putExtra(ChatActivity.bundleReceiverEmail, otherUserEmail);
        chatActivityRule.launchActivity(intent);

        onView(withText("Hello!")).check(matches(isDisplayed()));
        onView(withText("Hi!")).check(matches(isDisplayed()));
    }

    @Test
    public void sendNewMessage() {
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.bundleListingId,listingID);
        intent.putExtra(ChatActivity.bundleReceiverEmail, otherUserEmail);
        chatActivityRule.launchActivity(intent);

        String message = "I'm interested";
        onView(withId(R.id.messageEditor)).perform(typeText(message));
        onView(withId(R.id.sendMessageButton)).perform(click());
        onView(withText(message)).check(matches(isDisplayed()));
    }



}