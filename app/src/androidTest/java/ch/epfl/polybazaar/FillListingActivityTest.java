package ch.epfl.polybazaar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import androidx.fragment.app.Fragment;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.UI.SalesOverview;

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
import static ch.epfl.polybazaar.Utilities.convertDrawableToBitmap;
import static ch.epfl.polybazaar.Utilities.convertFileToString;
import static ch.epfl.polybazaar.Utilities.convertStringToBitmap;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useMockNetworkState;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.useRealNetwork;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)

public class FillListingActivityTest {
    static Uri imageUri;
    static Bitmap imageBitmap;
    static Intent galleryIntent;
    static Intent cameraIntent;

    Instrumentation.ActivityResult galleryResult;
    Instrumentation.ActivityResult cameraResult;
    static Matcher<Intent> expectedGalleryIntent;
    static Matcher<Intent> expectedCameraIntent;

    @Rule
    public final ActivityTestRule<FillListingActivity> fillSaleActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void init() {
        useMockDataStore();
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
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(imageUri.hashCode()))));
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
        Thread.sleep(2000);
        }

    @Test
    public void toastAppearsWhenPriceIsEmpty() throws Throwable {
        selectCategory("Furniture");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(2000);
    }

    @Test
    public void toastAppearsWhenNoCategoryIsSelected() throws Throwable {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(2000);
    }

    @Test
    public void toastAppearsWhenNoSubCategoryIsSelected() throws Throwable {
        selectCategory("Multimedia");
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("My title"));
        closeSoftKeyboard();
        onView(withId(R.id.priceSelector)).perform(scrollTo(), clearText());
        submitListingAndCheckIncorrectToast();
        Thread.sleep(2000);
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
        Thread.sleep(5000);
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void testUtilitiesConvertAndReverseConversion() {
        convertStringToBitmap(convertBitmapToString(convertDrawableToBitmap(ContextCompat.getDrawable(fillSaleActivityTestRule.getActivity(), R.drawable.algebre_lin))));
    }

    @Test
    public void testUtilitiesConvertFileToString() {
        assertThat(convertFileToString(null), is(""));
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
        Thread.sleep(5000);
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
        Thread.sleep(5000);
        runOnUiThread(() -> {
            DialogFragment dialogFragment = (DialogFragment)fillSaleActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("noConnectionDialog");
            AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();
            Button posButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            posButton.performClick();
        });
        Thread.sleep(5000);
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
        Thread.sleep(5000);
        runOnUiThread(() -> {

            DialogFragment dialogFragment = (DialogFragment)fillSaleActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("noConnectionDialog");
            AlertDialog dialog = (AlertDialog) dialogFragment.getDialog();
            Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negButton.performClick();
        });
        //onView(withText("Cancel")).perform(click());
        Thread.sleep(5000);
        hasComponent(FillListingActivity.class.getName());
        Intents.release();
        useRealNetwork();
    }


    private void uploadImage(){
        closeSoftKeyboard();
        expectedGalleryIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        intending(expectedGalleryIntent).respondWith(galleryResult);
        onView(withId(R.id.uploadImage)).perform(scrollTo(), click());
        intended(expectedGalleryIntent);
        Intents.release();
    }


    private void submitListingAndCheckIncorrectToast() throws Throwable {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Button but = fillSaleActivityTestRule.getActivity().findViewById(R.id.submitListing);
                but.performClick();
            }
        });

        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT)).inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
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
