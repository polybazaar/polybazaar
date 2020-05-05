package ch.epfl.polybazaar;
import android.content.Context;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class UtilitiesTest {
    MockAuthenticator auth;
    Context context;

    @BeforeClass
    public void init() {
        auth = MockAuthenticator.getInstance();
        AuthenticatorFactory.setDependency(auth);
    }

    @AfterClass
    public void reset(){
        MockAuthenticator.getInstance().reset();
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
