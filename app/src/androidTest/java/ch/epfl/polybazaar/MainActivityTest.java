package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.login.SignInActivity;

import ch.epfl.polybazaar.UI.SalesOverview;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest{

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };

    @Test
    public void authenticatedUsersCanEnterFillListing() {
        signInAndBack();
        onView(withId(R.id.addListing)).perform(click());
        hasComponent(FillListingActivity.class.getName());
    }

    @Test
    public void authenticatedUsersCanSignOut() {
        signInAndBack();
        onView(withText(R.string.sign_out)).check(matches(isDisplayed()));

        onView(withId(R.id.authenticationButton)).perform(click());

        onView(withText(R.string.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void accessesForNonAuthenticatedUserAreCorrect() {
        onView(withId(R.id.saleOverview)).perform(click());
        hasComponent(SalesOverview.class.getName());

        pressBack();

        onView(withId(R.id.addListing)).perform(click());
        onView(withText(R.string.sign_in_required))
                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withId(R.id.authenticationButton)).perform(click());
        hasComponent(SignInActivity.class.getName());

    }

    private void signInAndBack() {
        onView(withId(R.id.authenticationButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(MockAuthenticator.TEST_USER_EMAIL))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText(MockAuthenticator.TEST_USER_PASSWORD))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
    }
}