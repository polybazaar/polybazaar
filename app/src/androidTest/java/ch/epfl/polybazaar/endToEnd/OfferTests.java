package ch.epfl.polybazaar.endToEnd;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;


import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SubmitOffer;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.Matchers.is;

public class OfferTests {

    public static final int SLEEP_TIME = 2000;

    private final String Listing1Name = "LOL";
    private final String Listing1Price = "123";
    private final String TestUser2Email = "test.magicuser@epfl.ch";
    private final String TestUser2NickName = "lolMan";
    private final String TestUser2Password = "password123";
    private Listing listing1;
    private String testListing1ID;

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    useMockDataStore();
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    User testUser1 = new User("nickname", MockAuthenticator.TEST_USER_EMAIL);
                    testUser1.save();
                    MockAuthenticator.getInstance().createUser(TestUser2Email, TestUser2NickName, TestUser2Password);
                    User testUser2 = new User(TestUser2NickName, TestUser2Email);
                    testUser2.save();
                    Listing testListing1 = new Listing(Listing1Name, "", Listing1Price, MockAuthenticator.TEST_USER_EMAIL, "none");
                    testListing1.saveWithLiteVersion();
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listing1 = testListing1;
                    ChatMessage message = new ChatMessage(TestUser2Email, MockAuthenticator.TEST_USER_EMAIL,
                            testListing1.getId(), ChatMessage.OFFER_MADE+ "15.0", new Timestamp(12, 13));
                    message.save();
                    testListing1ID = testListing1.getId();
                    MockAuthenticator.getInstance().signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().signOut();
                    MockAuthenticator.getInstance().reset();
                }
            };

    @Test
    public void doOfferTest() throws InterruptedException {
        MockAuthenticator.getInstance().signOut();
        MockAuthenticator.getInstance().signIn(TestUser2Email, TestUser2Password);
        onView(withId(R.id.action_home)).perform(click());
        onView(withText(Listing1Name)).perform(click());
        Thread.sleep(SLEEP_TIME);
        doAllOfferOptions();
        ChatMessage.fetchMessagesFrom(TestUser2Email).addOnSuccessListener(chatMessages -> {
            assertThat(chatMessages.get(0).getMessage(), is(ChatMessage.OFFER_MADE+ "15.0"));
        });
        pressBack();
    }

    @Test
    public void sendOfferTest() {
        MockAuthenticator.getInstance().signOut();
        MockAuthenticator.getInstance().signIn(TestUser2Email, TestUser2Password);
        SubmitOffer.sendOffer(15.0, listing1, activityRule.getActivity());
        ChatMessage.fetchMessagesFrom(TestUser2Email).addOnSuccessListener(chatMessages -> {
            assertThat(chatMessages.get(0).getMessage(), is(ChatMessage.OFFER_MADE+ "15.0"));
        });
    }

    @Test
    public void acceptOfferTest() {
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText(Listing1Name)).perform(click());
        onView(withId(R.id.accept_offer_button)).perform(click());
        Listing.fetch(testListing1ID).addOnSuccessListener(listing -> {
            assertThat(listing.getListingActive(), is(false));
            assertThat(listing.getPrice(), is(activityRule.getActivity().getString(R.string.sold)));
        });
        LiteListing.fetch(testListing1ID).addOnSuccessListener(listing -> {
            assertThat(listing.getPrice(), is(activityRule.getActivity().getString(R.string.sold)));
        });
    }

    @Test
    public void refuseOfferTest() {
        ChatMessage message = new ChatMessage(TestUser2Email, MockAuthenticator.TEST_USER_EMAIL,
                testListing1ID, ChatMessage.OFFER_PROCESSED + ChatMessage.OFFER_ACCEPTED + "15.0", new Timestamp(12, 13));
        message.save();
        ChatMessage message1 = new ChatMessage(TestUser2Email, MockAuthenticator.TEST_USER_EMAIL,
                testListing1ID, ChatMessage.OFFER_PROCESSED + ChatMessage.OFFER_REFUSED + "15.0", new Timestamp(12, 13));
        message1.save();
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText(Listing1Name)).perform(click());
    }

    @Test
    public void makeOfferTest() {
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText(Listing1Name)).perform(click());
        onView(withId(R.id.refuse_offer_button)).perform(click());
        Listing.fetch(testListing1ID).addOnSuccessListener(listing -> {
            assertThat(listing.getListingActive(), is(true));
        });
    }

    private void doAllOfferOptions() {
        closeSoftKeyboard();
        onView(withId(R.id.contactSel)).perform(scrollTo(), click());
        pressBack();
        //onView(withId(R.id.buyNow)).perform(scrollTo(), click());
        //pressBack();
        onView(withId(R.id.makeOffer)).perform(scrollTo(), click());
        onView(withId(R.id.offer)).perform(typeText("12"));
        closeSoftKeyboard();
        onView(withId(R.id.cancelOfferMaking)).perform(click());
        onView(withId(R.id.makeOffer)).perform(scrollTo(), click());
        onView(withId(R.id.offer)).perform(typeText(""));
        closeSoftKeyboard();
        onView(withId(R.id.makeOfferNow)).perform(click());
        onView(withId(R.id.offer)).perform(typeText("15"));
        closeSoftKeyboard();
        onView(withId(R.id.makeOfferNow)).perform(click());
        pressBack();
    }
}
