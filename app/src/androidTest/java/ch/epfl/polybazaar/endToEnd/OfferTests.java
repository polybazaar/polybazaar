package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
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
    public void buyNowTest() {
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_add_item)).perform(click());
        closeSoftKeyboard();
        addItemFromFillListing();
        signOutFromMainActivity();
        onView(withId(R.id.action_add_item)).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        createAccountAndBackToLoginFromLoginActivity("test.magicuser@epfl.ch", "lolMan", "password123");
        signInWithFromMainActivity("test.magicuser@epfl.ch", "password123");
        onView(withText("LOL")).perform(click());
        onView(withId(R.id.buyNow)).perform(click());
    }

    @Test
    public void acceptOfferTest() {
        setupListingAndConversation();
        onView(withId(R.id.accept_offer_button)).perform(click());
        pressBack();
        onView(withId(R.id.action_home)).perform(click());
        onView(withText(activityRule.getActivity().getString(R.string.sold))).perform(click());
        onView(withId(R.id.price)).check(matches(withText(activityRule.getActivity().getString(R.string.sold))));
        onView(withId(R.id.makeOffer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.buyNow)).check(matches(not(isDisplayed())));
    }

    @Test
    public void refuseOfferTest() {
        setupListingAndConversation();
        onView(withId(R.id.refuse_offer_button)).perform(click());
        pressBack();
        onView(withId(R.id.action_home)).perform(click());
        onView(withText("CHF 123")).perform(click());
        onView(withId(R.id.price)).check(matches(withText("CHF 123")));
    }

    private void addItemFromFillListing() {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("LOL"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        closeSoftKeyboard();
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
    }

    private void signOutFromMainActivity() {
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signOutButton)).perform(scrollTo(), click());
    }

    private void setupListingAndConversation(){
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_add_item)).perform(click());
        closeSoftKeyboard();
        addItemFromFillListing();
        signOutFromMainActivity();
        onView(withId(R.id.action_add_item)).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        createAccountAndBackToLoginFromLoginActivity("test.magicuser@epfl.ch", "lolMan", "password123");
        signInWithFromMainActivity("test.magicuser@epfl.ch", "password123");
        onView(withText("LOL")).perform(click());
        onView(withId(R.id.makeOffer)).perform(click());
        onView(withId(R.id.offer)).perform(typeText("12"));
        onView(withId(R.id.cancelOfferMaking)).perform(click());
        onView(withId(R.id.makeOffer)).perform(click());
        onView(withId(R.id.offer)).perform(typeText("15"));
        onView(withId(R.id.makeOfferNow)).perform(click());
        pressBack();
        signOutFromMainActivity();
        signInWithFromMainActivity(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        onView(withId(R.id.action_messages)).perform(click());
        onView(withText("LOL")).perform(click());
    }
}
