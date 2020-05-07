package ch.epfl.polybazaar;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;


public class FavoritesTest {


    MockAuthenticator auth;

    @Rule
    public final ActivityTestRule<SalesOverview> mActivityRule =
            new ActivityTestRule<SalesOverview>(SalesOverview.class){
            };


    @Before
    public void init() {
        useMockDataStore();
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }


    /*@Test
    public void favoritesUserNotLoggedIn() {
        clickButton(withId(R.id.favoritesOverview));

        onView(withText(R.string.sign_in_required))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }*/

   /* @Test public void favoritesListIsEmpty() {
        auth.signIn("test.user@epfl.ch", "abcdef");
        clickButton(withId(R.id.favoritesOverview));

        onView(withText(R.string.no_favorites))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }*/
    
    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }


}
