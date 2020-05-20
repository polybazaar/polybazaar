package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseChecksUtilities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities.storeNewListing;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.signInWithFromMainActivity;


public class EditAndDeleteTest {


    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                    useMockCategory();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
    };

}
