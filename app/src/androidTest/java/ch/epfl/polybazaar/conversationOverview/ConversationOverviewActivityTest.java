package ch.epfl.polybazaar.conversationOverview;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.filestorage.FileStoreFactory;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.filestorage.LocalCache;
import ch.epfl.polybazaar.filestorage.MockFileStore;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.AuthenticatorResult;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertDrawableToBitmap;
import static com.google.android.gms.tasks.Tasks.whenAll;
import static java.util.UUID.randomUUID;

public class ConversationOverviewActivityTest {

    private String newListingID;

    @Rule
    public final ActivityTestRule<ConversationOverviewActivity> conversationOverviewActivityRule =
            new ActivityTestRule<ConversationOverviewActivity>(ConversationOverviewActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                    useMockCategory();
                    FileStoreFactory.setDependency(MockFileStore.getInstance());
                    LocalCache.setRoot("test-cache");

                    Task<AuthenticatorResult> loginTask = AuthenticatorFactory.getDependency()
                            .signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
                    DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, MockAuthenticator.TEST_USER_EMAIL);
                    newListingID = randomUUID().toString();
                    LiteListing liteListing = new LiteListing(newListingID, "Title", "0", "");
                    Task<Void> liteListingTask = liteListing.save();

                    ChatMessage message = new ChatMessage("user_email@epfl.ch", MockAuthenticator.TEST_USER_EMAIL, newListingID, "messageText", new Timestamp(new Date(System.currentTimeMillis())));
                    final String newMessageID = randomUUID().toString();
                    message.setId(newMessageID);
                    Task<Void> messageTask = message.save();

                    whenAll(liteListingTask, loginTask, messageTask);
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                    MockFileStore.getInstance().cleanUp();
                    LocalCache.cleanUp(InstrumentationRegistry.getInstrumentation().getContext());
                }
            };

    @Test
    public void testConversationIsVisible() {
        conversationOverviewActivityRule.launchActivity(new Intent());
        String nextId = randomUUID().toString();
        List<String> list = new ArrayList<>();
        list.add(nextId);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap img = Bitmap.createBitmap(500, 300, conf);
        Task<Void> storeImage = ImageTransaction.store(nextId, img, 100, conversationOverviewActivityRule.getActivity().getApplicationContext());
        Task<Void> updateListing = LiteListing.updateField(LiteListing.THUMBNAIL_REF, newListingID, list);
        whenAll(storeImage, updateListing);
        conversationOverviewActivityRule.finishActivity();
        conversationOverviewActivityRule.launchActivity(new Intent());
        onView(withId(R.id.title_conversation))
                .check(matches(withText("Title")));
    }

}