package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.createAccountAndBackToLoginFromLoginActivity;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.signInWithFromMainActivity;
import static org.hamcrest.Matchers.not;

public class OfferTests {

    public static final int SLEEP_TIME = 2000;

    private final String Listing1Name = "LOL";
    private final String Listing1Price = "123";
    private final String Listing2Name = "FUN";
    private final String Listing2Price = "321";

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    useMockDataStore();
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    User testUser = new User("nickname", MockAuthenticator.TEST_USER_EMAIL);
                    testUser.save();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };

    @Test
    public void refuseThenAcceptOfferTest() throws InterruptedException {
        // setup
        onView(withId(R.id.action_profile)).perform(click());
        createAccountAndBackToLoginFromLoginActivity("test.magicuser@epfl.ch", "lolMan", "password123");
        setupListingAndConversation(Listing1Name, Listing1Price);
        // refuse offer
        doAllOfferOptions();
        gotToConversation(Listing1Name);
        onView(withId(R.id.refuse_offer_button)).perform(click());
        pressBack();
        onView(withId(R.id.action_home)).perform(click());
        onView(withText("CHF " + Listing1Price)).perform(click());
        onView(withId(R.id.price)).check(matches(withText("CHF " + Listing1Price)));
        pressBack();
        // accept offer
        signOutFromMainActivity();
        setupListingAndConversation(Listing2Name, Listing2Price);
        onView(withId(R.id.makeOffer)).perform(click());
        onView(withId(R.id.offer)).perform(typeText("15"));
        onView(withId(R.id.makeOfferNow)).perform(click());
        pressBack();
        gotToConversation(Listing2Name);
        onView(withId(R.id.accept_offer_button)).perform(click());
        pressBack();
        onView(withId(R.id.action_home)).perform(click());
        onView(withText(activityRule.getActivity().getString(R.string.sold))).perform(click());
        onView(withId(R.id.price)).check(matches(withText(activityRule.getActivity().getString(R.string.sold))));
        onView(withId(R.id.makeOffer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.buyNow)).check(matches(not(isDisplayed())));
    }

    private void addItemFromFillListing(String name, String price) {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText(name));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText(price));
        closeSoftKeyboard();
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
    }

    private void signOutFromMainActivity() {
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signOutButton)).perform(scrollTo(), click());
    }


    private void setupListingAndConversation(String name, String price){
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_add_item)).perform(click());
        closeSoftKeyboard();
        addItemFromFillListing(name, price);
        signOutFromMainActivity();
        signInWithFromMainActivity("test.magicuser@epfl.ch", "password123");
        onView(withText(name)).perform(click());
    }

    private void gotToConversation(String name) throws InterruptedException {
        signOutFromMainActivity();
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText(name)).perform(click());
        Thread.sleep(SLEEP_TIME);
    }

    private void doAllOfferOptions() {
        closeSoftKeyboard();
        onView(withId(R.id.buyNow)).perform(scrollTo(), click());
        pressBack();
        onView(withId(R.id.makeOffer)).perform(scrollTo(), click());
        onView(withId(R.id.offer)).perform(typeText("12"));
        onView(withId(R.id.cancelOfferMaking)).perform(click());
        onView(withId(R.id.makeOffer)).perform(scrollTo(), click());
        onView(withId(R.id.offer)).perform(typeText("15"));
        onView(withId(R.id.makeOfferNow)).perform(scrollTo(), click());
        pressBack();
    }
}
