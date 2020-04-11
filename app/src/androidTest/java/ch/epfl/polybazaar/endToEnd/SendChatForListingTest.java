package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.message.ChatMessage;
import ch.epfl.polybazaar.testingUtilities.DatabaseChecksUtilities;
import ch.epfl.polybazaar.testingUtilities.SignInUtilities;
import ch.epfl.polybazaar.testingUtilities.StoreListingUtilities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;

public class SendChatForListingTest {
    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };


    @Test
    public void testUserCanSendMessageViaListing(){
        String otherUserEmail = "other.user@epfl.ch";
        String title = "Send chat";
        String message = "Hello how are you?";
        SignInUtilities.signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        StoreListingUtilities.storeNewListing(title, otherUserEmail);

        onView(withId(R.id.saleOverview)).perform(click());

        onView(withText(title)).perform(click());

        //This special construct is needed as the contactSel button may take some time to appear
        //This could probably be done better, with the use of a callback
        boolean passed = false;
        do{
            try{
                onView(withId(R.id.contactSel)).perform(scrollTo(), click());
                passed = true;
            }
            catch (Exception e){}
        }while(!passed);

        onView(withId(R.id.messageEditor)).perform(typeText(message));
        closeSoftKeyboard();
        onView(withId(R.id.sendMessageButton)).perform(click());

        DatabaseChecksUtilities.assertDatabaseHasAtLeastOneEntryWithField(ChatMessage.COLLECTION, "message", message, ChatMessage.class);

    }
}
