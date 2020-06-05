package ch.epfl.polybazaar;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.epfl.polybazaar.filestorage.LocalCache;
import ch.epfl.polybazaar.filestorage.MockFileStore;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class UtilitiesTest {
    MockAuthenticator auth;
    Context context;

    @Before
    public void init() {
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }

    @After
    public void reset(){
        MockAuthenticator.getInstance().reset();
        MockFileStore.getInstance().cleanUp();
        LocalCache.cleanUp(InstrumentationRegistry.getInstrumentation().getContext());
    }

    @Test
    public void testGetUserLoggedIn() {
        auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        Account user = Utilities.getUser();
        assertEquals(MockAuthenticator.TEST_USER_EMAIL, user.getEmail());
        assertEquals(MockAuthenticator.TEST_USER_NICKNAME, user.getNickname());
        assertTrue(Utilities.checkUserLoggedIn(context));
    }

    @Test
    public void testUserGetNotLoggedIn() {
        auth.signOut();
        Account user = Utilities.getUser();
        assertEquals(null, user);
    }
}
