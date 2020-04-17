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

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.filllisting.FillListingActivity;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
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
import static ch.epfl.polybazaar.Utilities.convertBitmapToString;
import static ch.epfl.polybazaar.Utilities.convertBitmapToStringWithQuality;
import static ch.epfl.polybazaar.Utilities.convertDrawableToBitmap;
import static ch.epfl.polybazaar.Utilities.convertFileToString;
import static ch.epfl.polybazaar.Utilities.convertStringToBitmap;
import static ch.epfl.polybazaar.Utilities.resizeBitmap;
import static ch.epfl.polybazaar.Utilities.resizeStringImageThumbnail;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_EMAIL;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_PASSWORD;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useMockNetworkState;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useRealNetwork;
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

public class FillListingActivityTest {

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
    public final ActivityTestRule<FillListingActivity> fillSaleActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void init() {
        useMockDataStore();
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        AuthenticatorFactory.getDependency().signIn(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        Activity activityUnderTest = fillSaleActivityTestRule.getActivity();
        activityUnderTest.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }

    @After
    public void unSigned() {
        AuthenticatorFactory.getDependency().signOut();
    }


    @BeforeClass
    public static void setupStubIntent(){
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        imageBitmap = BitmapFactory.decodeResource(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        
        cameraIntent = new Intent();
        cameraIntent.putExtra("data", imageBitmap);

    }

    @Test
    public void testFreeSwitchFreezesPriceSelector() {
        onView(withId(R.id.freeSwitch)).perform(scrollTo(), click());
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        onView(withId(R.id.priceSelector)).check(matches(withText("0.0")));
    }

    @Test
    public void testPriceSelectorRemembersPriceAfterFreeSwitchDisabled() {
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123.45"));
        onView(withId(R.id.freeSwitch)).perform(click());
        onView(withId(R.id.priceSelector)).check(matches(withText("0.0")));
        onView(withId(R.id.freeSwitch)).perform(click());
        onView(withId(R.id.priceSelector)).check(matches(withText("123.45")));
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
        selectCategory("Furniture");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText());
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        submitListingAndCheckIncorrectToast();
        Thread.sleep(SLEEP_TIME);
        }

    @Test
    public void toastAppearsWhenPriceIsEmpty() throws Throwable {
        selectCategory("Furniture");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void toastAppearsWhenNoCategoryIsSelected() throws Throwable {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void toastAppearsWhenNoSubCategoryIsSelected() throws Throwable {
        selectCategory("Multimedia");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(SLEEP_TIME);
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
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        selectCategory("Furniture");
        onView(withId(R.id.descriptionSelector)).perform(scrollTo(), typeText("That is a loooong description    yada yada yada hahahahaha      much long very description"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        closeSoftKeyboard();
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
        assertNull(resizeStringImageThumbnail(null));
    }

    @Test
    public void testRemoveImage() throws Throwable {
        uploadMultipleImages();
        String firstImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.modifyImage).performClick();
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).performClick();
        });
        String newFirstImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();

        //check that it delete last image
        assertNotEquals(newFirstImage, firstImage);
    }

    @Test
    public void testRotateImage() throws Throwable {
        uploadMultipleImages();

        String beforeRotationImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.modifyImage).performClick();
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.rotateLeft).performClick();
        });
        String afterRotationImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();

        assertNotEquals(afterRotationImage, beforeRotationImage);
    }

    @Test
    public void testMoveImageFirstAndSubmit() throws Throwable {
        uploadImage();
        //this do nothing because there only one image
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.modifyImage).performClick();
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.setFirst).performClick();
        });
        uploadMultipleImages();
        String firstImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();
        runOnUiThread(() -> {
            fillSaleActivityTestRule.getActivity().findViewById(R.id.modifyImage).performClick();
            assertTrue(fillSaleActivityTestRule.getActivity().findViewById(R.id.deleteImage).isClickable());
            fillSaleActivityTestRule.getActivity().findViewById(R.id.setFirst).performClick();
        });
        String newFirstImage = fillSaleActivityTestRule.getActivity().getCurrentStringImage();
        //check that it's equal because viewPager show the first image
        assertEquals(newFirstImage, firstImage);

        fillListing();

        Intents.init();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }


    public void cancelTakingPicture() throws Throwable {
        cameraResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, cameraIntent);
        closeSoftKeyboard();
        Intents.init();
        expectedCameraIntent = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intending(expectedCameraIntent).respondWith(cameraResult);

        runOnUiThread(() -> {
            Button but = fillSaleActivityTestRule.getActivity().findViewById(R.id.camera);
            but.performClick();
        });
        fillSaleActivityTestRule.getActivity().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        intended(expectedCameraIntent);
        Intents.release();
    }


    @Test
    public void DialogAppearsWhenNoConnection() throws Throwable {
        Intents.init();
        useMockNetworkState(false);
        useMockDataStore();
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
        hasComponent(FillListingActivity.class.getName());
        Intents.release();
        useRealNetwork();
    }

   /* @Test
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
        AppUser authAccount = auth.getCurrentUser();
        authAccount.getUserData().addOnSuccessListener(user -> {
            if (user != null) {
                ArrayList<String> ownListings = user.getOwnListings();
                assertNotEquals(null, ownListings.get(0));
            }
        });
    }*/


    @Test
    public void testCreateAndSendListingWhenUserNull() throws Throwable {
        MockAuthenticator.getInstance().signOut();
        fillListing();

        Intents.init();
        runOnUiThread(() -> fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing).performClick());
        //wait for MainActivity
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();

        //sign in again for remaining tests
        whenAll(MockAuthenticator.getInstance().signIn(TEST_USER_EMAIL, TEST_USER_PASSWORD));

    }

    private void uploadImage(){
        closeSoftKeyboard();
        expectedGalleryIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        intending(expectedGalleryIntent).respondWith(galleryResult);
        try {
            runOnUiThread(() -> {
                fillSaleActivityTestRule.getActivity().findViewById(R.id.uploadImage).performClick();
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        intended(expectedGalleryIntent);
        Intents.release();
    }

    private void uploadMultipleImages() {
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
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
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Button but = fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing);
                but.performClick();
            }
        });

        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT))
                .inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    private void checkNoImageUploaded(){
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    private void selectCategory(String cat){
        onView(withId(R.id.categorySelector)).perform(scrollTo(), click());
        onData(hasToString(cat)).perform(click());
    }
    private void fillListing() throws Throwable {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        selectCategory("Furniture");
        onView(withId(R.id.descriptionSelector)).perform(scrollTo(), typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        closeSoftKeyboard();
    }

}
