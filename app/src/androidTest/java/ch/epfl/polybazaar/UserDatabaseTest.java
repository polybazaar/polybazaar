package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.callback.UserCallback;
import ch.epfl.polybazaar.database.callback.UserCallbackAdapter;
import ch.epfl.polybazaar.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
}
