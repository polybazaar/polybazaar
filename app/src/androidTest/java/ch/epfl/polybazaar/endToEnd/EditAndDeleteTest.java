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
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.signInWithFromMainActivity;
import static ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities.storeNewListing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public class EditAndDeleteTest {


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
    public void testDeleteWorksForPreviouslyUploadedListing() throws InterruptedException {
        String title = "My delete test";
        String email = MockAuthenticator.TEST_USER_EMAIL;
        storeNewListing(title, email);
        signInWithFromMainActivity(email, MockAuthenticator.TEST_USER_PASSWORD);

        onView(withId(R.id.saleOverview)).perform(click());
        onView(withText(title)).perform(click());
        onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
        onView(withText("Yes")).perform(click());

        DatabaseChecksUtilities.assertDatabaseHasNoEntryWithField(Listing.COLLECTION, "title", title, Listing.class);
    }

    @Test
    public void testEditWorksForPreviouslyUploadedListing() throws Throwable {
        String oldTitle = "My test";
        String newTitle = "My edited test";
        String email = MockAuthenticator.TEST_USER_EMAIL;
        storeNewListing(oldTitle, email);
        signInWithFromMainActivity(email, MockAuthenticator.TEST_USER_PASSWORD);

        onView(withId(R.id.saleOverview)).perform(click());

        onView(withText(oldTitle)).perform(click());
        onView(withId(R.id.editButton)).perform(scrollTo(), click());
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText(), typeText(newTitle));
        closeSoftKeyboard();
        onView(withId(R.id.categorySelector)).perform(scrollTo(), click());
        onView(withText("Others")).perform(scrollTo(), click());
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());

        DatabaseChecksUtilities.assertDatabaseHasAtLeastOneEntryWithField(Listing.COLLECTION, "title", newTitle, Listing.class);
        DatabaseChecksUtilities.assertDatabaseHasNoEntryWithField(Listing.COLLECTION, "title", oldTitle, Listing.class);

    }
}
