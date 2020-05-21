package ch.epfl.polybazaar;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.polybazaar.UI.NotSignedIn;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static junit.framework.TestCase.assertEquals;


public class FavoritesTest {


    @Rule
    public final ActivityTestRule<NotSignedIn> mActivityRule =
            new ActivityTestRule<NotSignedIn>(NotSignedIn.class){
            };
    MockAuthenticator auth;

    @Before
    public void init() {
        useMockDataStore();
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }

    @After
    public void reset(){
        MockAuthenticator.getInstance().reset();
    }


   @Test public void favoritesListIsEmpty() {
        auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        clickButton(withId(R.id.signInButton));

        clickButton(withId(R.id.action_profile));
        clickButton(withId(R.id.viewFavoritesButton));
        
        onView(withText(R.string.no_favorites)).check(matches(isDisplayed()));
    }

    @Test public void favoriteIsRecorded() throws Throwable {
        Listing listing1 = new Listing();
        auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);

        Authenticator auth = AuthenticatorFactory.getDependency();
        Account authAccount = auth.getCurrentUser();

        authAccount.getUserData().addOnSuccessListener(user -> {
            user.addFavorite(listing1.getId());
            List<String> favoritesIds = user.getFavorites();
            assertEquals(listing1.getId(), favoritesIds.get(0));
        });

    }
    
    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }


}
