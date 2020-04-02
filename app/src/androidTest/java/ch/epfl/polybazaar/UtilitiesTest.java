package ch.epfl.polybazaar;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.polybazaar.login.AppUser;
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

    @Test
    public void testGetUserLoggedIn() {
        auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
        AppUser user = Utilities.getUser();
        assertEquals(MockAuthenticator.TEST_USER_EMAIL, user.getEmail());
        assertEquals(MockAuthenticator.TEST_USER_NICKNAME, user.getNickname());
        assertTrue(Utilities.checkUserLoggedIn(context));
    }

    @Test
    public void testGetUserNotLoggedIn() {
        auth.signOut();
        AppUser user = Utilities.getUser();
        assertEquals(null, user);
    }

}
