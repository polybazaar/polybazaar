package ch.epfl.polybazaar;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public class FavoritesTest {

    MockAuthenticator auth;

    @Rule
    public final ActivityTestRule<SalesOverview> mActivityRule =
            new ActivityTestRule<SalesOverview>(SalesOverview.class){
                /*@Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                }

                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                    MockPhoneSettings.getInstance().setAirPlaneMode(false);
                }*/
            };

    @Before 
    public void init() {
        useMockDataStore();
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }


    @Test
    public void favoritesUserNotLoggedIn() {
        clickButton(withId(R.id.favoritesOverview));

        onView(withText(R.string.sign_in_required))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }



    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }


}
