package ch.epfl.polybazaar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.filestorage.FileStoreFactory;
import ch.epfl.polybazaar.filestorage.LocalCache;
import ch.epfl.polybazaar.filestorage.MockFileStore;
import ch.epfl.polybazaar.filllisting.ImageManager;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static ch.epfl.polybazaar.category.RootCategoryFactory.useMockCategory;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.filllisting.ImageManager.MAXIMUM_IMAGES_NUMBER;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_EMAIL;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_PASSWORD;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useMockNetworkState;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useRealNetwork;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToString;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringWithQuality;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertDrawableToBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertFileToString;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.resizeBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.resizeImageThumbnail;
import static com.google.android.gms.tasks.Tasks.whenAll;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class FillListingTest {

    private final int SLEEP_TIME = 2000;

    static Uri imageUri;
    static Bitmap imageBitmap;
    static Intent galleryIntent;
    static Intent cameraIntent;

    Instrumentation.ActivityResult galleryResult;
    Instrumentation.ActivityResult cameraResult;
    static Matcher<Intent> expectedGalleryIntent;
    static Matcher<Intent> expectedCameraIntent;

    MockAuthenticator auth;


    @Rule
    public final ActivityTestRule<FillListing> fillSaleActivityTestRule = new ActivityTestRule<FillListing>(FillListing.class) {


    @Override
    protected void beforeActivityLaunched() {
        useMockCategory();
        useMockDataStore();
        FileStoreFactory.setDependency(MockFileStore.getInstance());
        LocalCache.setRoot("test-cache");
    }
};

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void init() {

        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        AuthenticatorFactory.getDependency().signIn(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, TEST_USER_EMAIL);

        Activity activityUnderTest = fillSaleActivityTestRule.getActivity();
        activityUnderTest.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);

    }

    @After
    public void unSigned() {
        AuthenticatorFactory.getDependency().signOut();
        MockAuthenticator.getInstance().reset();
        MockFileStore.getInstance().cleanUp();
        LocalCache.cleanUp(InstrumentationRegistry.getInstrumentation().getContext());
    }


    @BeforeClass
    public static void setupStubIntent(){

        Resources resources = getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        imageBitmap = BitmapFactory.decodeResource(
                getInstrumentation().getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        
        cameraIntent = new Intent();
        cameraIntent.putExtra("data", imageBitmap);

    }

    @Test
    public void testUploadPictureCorrectly(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
        try {
            runOnUiThread(()->assertNotEquals(null, fillSaleActivityTestRule.getActivity().findViewById(R.id.picturePreview).getDrawableState()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testUploadPictureFailsWhenUserCancels(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, galleryIntent);
        uploadImage();
        checkNoImageUploaded();
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNull(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, null);
        uploadImage();
        checkNoImageUploaded();
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNullAndUserCancels(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, null);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    @Test
    public void toastAppearsWhenTitleIsEmpty() throws Throwable {
        selectCategory("Others");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText());
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        Thread.sleep(SLEEP_TIME);
        submitListingAndCheckIncorrectToast();
        }

    @Test
    public void toastAppearsWhenPriceIsEmpty() throws Throwable {
        selectCategory("Others");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        Thread.sleep(SLEEP_TIME);
        submitListingAndCheckIncorrectToast();
    }

    @Test
    public void testNoPictureIsDisplayedWhenNoPictureIsTaken() throws Throwable {
        cancelTakingPicture();
        checkNoImageUploaded();
        fillSaleActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }


    @Test
    public void submittingNewListingRedirectsToSalesOverview() throws Throwable {
        useMockDataStore();
        FileStoreFactory.setDependency(MockFileStore.getInstance());
        LocalCache.setRoot("test-cache");
        fillListing();
        Intents.init();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void testUtilitiesConvertAndReverseConversion() {
        convertStringToBitmap(convertBitmapToString(convertDrawableToBitmap(ContextCompat.getDrawable(fillSaleActivityTestRule.getActivity(), R.drawable.algebre_lin))));
    }

    @Test
    public void testUtilitiesConvertBitmapToString() {
        assertNull(convertBitmapToStringWithQuality(null, 100));
    }

    @Test
    public void testUtilitiesConvertFileToString() {
        assertThat(convertFileToString(null), is(""));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUtilitiesResizeBitmap() {
        assertNull(resizeBitmap(null, 0.5f, 0.5f));

        Bitmap bitmap = convertDrawableToBitmap(ContextCompat.getDrawable(fillSaleActivityTestRule.getActivity(), R.drawable.bicycle));
        assertEquals(bitmap.getHeight(), resizeBitmap(bitmap, 1, 1).getHeight());
        assertEquals(bitmap.getWidth(), resizeBitmap(bitmap, 1, 1).getWidth());

        //this should throw an IllegalArgumentException
        resizeBitmap(bitmap, 2f, 0.5f);
    }

    @Test
    public void testUtilitiesResizeStringImageThumbnailNull() {
        assertNull(resizeImageThumbnail(null));
    }

    @Test
    public void testRemoveImage() throws Throwable {
        uploadMultipleImages();
        Bitmap firstImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        runOnUiThread(() -> {
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).performClick();
        });
        Thread.sleep(SLEEP_TIME);
        Bitmap newFirstImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        //check that it delete last image
        assertNotEquals(newFirstImage, firstImage);
    }

    @Test
    public void testRotateImage() throws Throwable {
        uploadMultipleImages();
        Bitmap beforeRotationImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        runOnUiThread(() -> {
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.rotate).performClick();
        });
        Thread.sleep(SLEEP_TIME);
        Bitmap afterRotationImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        assertNotEquals(afterRotationImage, beforeRotationImage);
    }

    @Test
    public void testMoveImageFirstAndSubmit() throws Throwable {
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
        //this do nothing because there only one image
        runOnUiThread(() -> {
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.setMain).performClick();
        });
        Thread.sleep(SLEEP_TIME);
        uploadMultipleImages();
        Bitmap firstImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        runOnUiThread(() -> {
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.setMain).performClick();
        });
        Thread.sleep(SLEEP_TIME);
        Bitmap newFirstImage = fillSaleActivityTestRule.getActivity().getCurrentImage();
        //check that it's equal because viewPager show the first image
        assertEquals(newFirstImage, firstImage);
        fillListing();
        Intents.init();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void pressAddImage() throws Throwable {
        Thread.sleep(SLEEP_TIME);
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.addImage).performClick();
        });
        Thread.sleep(SLEEP_TIME);
        fillSaleActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public void cancelTakingPicture() throws Throwable {
        cameraResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, cameraIntent);
        closeSoftKeyboard();
        Intents.init();
        expectedCameraIntent = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intending(expectedCameraIntent).respondWith(cameraResult);
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.addImageFromCamera).performClick());
        Thread.sleep(SLEEP_TIME);
        fillSaleActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        Thread.sleep(SLEEP_TIME);
        intended(expectedCameraIntent);
        Intents.release();
    }

    @Test
    public void DialogAppearsWhenNoConnection() throws Throwable {
        Intents.init();
        useMockNetworkState(false);
        useMockDataStore();
        FileStoreFactory.setDependency(MockFileStore.getInstance());
        LocalCache.setRoot("test-cache");
        fillListing();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        assert(fillSaleActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("noConnectionDialog").isVisible());
        //onView(withText("No Internet connection found")).check(matches(isDisplayed()));
        Intents.release();
        useRealNetwork();
    }


    @Test
    public void DialogPositiveClickGoesToSalesOverview() throws Throwable {
        Intents.init();
        useMockNetworkState(false);
        useMockDataStore();
        FileStoreFactory.setDependency(MockFileStore.getInstance());
        LocalCache.setRoot("test-cache");
        fillListing();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        runOnUiThread(() -> {
            DialogFragment dialogFragment = (DialogFragment)fillSaleActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("noConnectionDialog");
            AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();
            Button posButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            posButton.performClick();
        });
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
        useRealNetwork();
    }

    @Test
    public void DialogNegativeClickGoesToFillListing() throws Throwable {
        Intents.init();
        useMockNetworkState(false);
        useMockDataStore();
        FileStoreFactory.setDependency(MockFileStore.getInstance());
        LocalCache.setRoot("test-cache");
        fillListing();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        runOnUiThread(() -> {
            DialogFragment dialogFragment = (DialogFragment)fillSaleActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("noConnectionDialog");
            AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();
            Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negButton.performClick();
        });
        Thread.sleep(SLEEP_TIME);
        hasComponent(FillListing.class.getName());
        Intents.release();
        useRealNetwork();
    }

    @Test
    public void newListingIsAddedToUserOwnListings() throws Throwable {
        auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        fillListing();
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Button but = fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing);
                but.performClick();
            }
        });
        auth.getCurrentUser().getUserData().addOnSuccessListener(user -> {
            if (user != null) {
                ArrayList<String> ownListings = user.getOwnListings();
                assertNotEquals(null, ownListings.get(0));
            }
        });
    }

    @Test
    public void testUploadMoreThanMaxNumberImages() {
        Resources resources = getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.drawable.bicycle) + '/' +
                resources.getResourceTypeName(R.drawable.bicycle) + '/' +
                resources.getResourceEntryName(R.drawable.bicycle));
        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        for(int i = 0; i <= MAXIMUM_IMAGES_NUMBER; i++) {
            uploadImage();
        }
        onView(withText(fillSaleActivityTestRule.getActivity().getString(R.string.max_num_images)))
                .check(matches(isDisplayed()));
        onView(withText(fillSaleActivityTestRule.getActivity().getString(R.string.back)))
                .perform(click());

    }

    private void uploadImage(){
        closeSoftKeyboard();
        expectedGalleryIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        intending(expectedGalleryIntent).respondWith(galleryResult);
        try {
            runOnUiThread(() -> {
                fillSaleActivityTestRule.getActivity().findViewById(R.id.addImageFromLibrary).performClick();
            });
            Thread.sleep(SLEEP_TIME);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        intended(expectedGalleryIntent);
        Intents.release();
    }

    private void uploadMultipleImages() {
        Resources resources = getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.drawable.bicycle) + '/' +
                resources.getResourceTypeName(R.drawable.bicycle) + '/' +
                resources.getResourceEntryName(R.drawable.bicycle));
        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.drawable.algebre_lin) + '/' +
                resources.getResourceTypeName(R.drawable.algebre_lin) + '/' +
                resources.getResourceEntryName(R.drawable.algebre_lin));
        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
    }


    private void submitListingAndCheckIncorrectToast() throws Throwable {
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick();
        });
        Thread.sleep(SLEEP_TIME/2);
        onView(withText(R.string.incorrect_fields))
                .inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        Thread.sleep(SLEEP_TIME);
    }

    private void checkNoImageUploaded(){
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    //always select the first category
    private void selectCategory(String cat) throws Throwable {
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.selectCategory).performClick());
        Thread.sleep(500);
        onView(withId(R.id.categoriesRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        pressBack();
        Thread.sleep(500);
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.categoryButton).performClick());
    }

    private void fillListing() throws Throwable {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        selectCategory("Others");
        onView(withId(R.id.descriptionSelector)).perform(scrollTo(), typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        closeSoftKeyboard();
    }

}
