package ch.epfl.polybazaar.user;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;

import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.user.UserDatabase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {

    @Test
    public void callUserCallback() {
        final User[] user = new User[1];
        UserCallback callback = new UserCallback() {
            @Override
            public void onCallback(User result) {
                user[0] = result;
            }
        };
        User inside = new User("me", "me.me@epfl.ch");
        callback.onCallback(inside);
        assertThat(user[0].getNickName(), is("me"));
        assertThat(user[0].getEmail(), is("me.me@epfl.ch"));
    }

    @Test
    public void callUserCallbackAdapter() {
        final User[] user = new User[1];
        UserCallback callback = new UserCallback() {
            @Override
            public void onCallback(User result) {
                user[0] = result;
            }
        };
        UserCallbackAdapter adapter = new UserCallbackAdapter(callback);
    }

    @Test
    public void callSuccessCallback() {
        final boolean[] res = {false};
        SuccessCallback callback = new SuccessCallback() {
            @Override
            public void onCallback(boolean result) {
                res[0] = result;
            }
        };
        callback.onCallback(true);
        assertThat(res[0], is(true));
    }

    private User receivedUser;
    @Test
    public void storeAndRetrieveUserTest(){
        useMockDataStore();
        User testUser = new User("testuser","test.me@epfl.ch");
        storeNewUser(testUser, result -> assertThat(result, is(true)));
        storeNewUser(testUser, result -> assertThat(result, is(false)));
        fetchUser("test.me@epfl.ch", result -> receivedUser = result);
        assertThat(receivedUser.getEmail(),is("test.me@epfl.ch"));
        assertThat(receivedUser.getNickName(),is("testuser"));
    }

    /*
    @Test
    public void deleteUserTest(){
        useMockDataStore();
        User testUser = new User("testuser","test.me@epfl.ch");
        storeNewUser(testUser, result -> assertThat(result, is(true)));
        deleteUser("testuser", result -> assertThat(result, is(true)));
        deleteUser("testId2", result -> assertThat(result, is(false)));
    }
    */

    @Test
    public void wrongUserIdReturnNull(){
        useMockDataStore();
        fetchUser("wrondId", Assert::assertNull);
    }
}
