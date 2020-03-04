package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.database.callback.UserCallback;
import ch.epfl.polybazaar.user.UserDatabase;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {

    private UserDatabase udb = new UserDatabase();

    private boolean success;

    //@Before
    @Test
    public void databaseInit() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);

        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User micheal = new User("Micheal", "Jaqueson", cal, "m.j@epfl.ch");
        cal.set(2001, 03, 30);
        User william = new User("William", "Shakespeare", cal, "w.s@epfl.ch");
        success = false;
        UserCallback callback = new UserCallback() {
            @Override
            public void onCallback(User result) {

            }

            @Override
            public void onCallback(boolean result) {
                success = true;
            }
        };
        udb.storeNewUser(micheal, callback);
        lock.await(2000, TimeUnit.MILLISECONDS);
        assertThat(success, is(true));
        success = false;
        udb.storeNewUser(william, callback);
        lock.await(2000, TimeUnit.MILLISECONDS);
        assertThat(success, is(true));
        success = false;
    }
/*
    @After
    public void databaseClean() {
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(true));
                //assertThat(udb.isSuccess(), is(false));  // to delete
            }
        };
        udb.deleteUser("mj@epfl.ch", fb, o);
        udb.deleteUser("ws@epfl.ch", fb, o);
    }


    @Test
    public void setNewUserAlreadyPresent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1913, 01, 11);
        User john = new User("John", "Jones", cal, "mj@epfl.ch");
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(false));
            }
        };
        udb.storeNewUser(john, fb, o);
    }

    @Test
    public void setNewUserNotPresent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1945, 11, 21);
        User john = new User("John", "Jones", cal, "jj@epfl.ch");
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(true));
            }
        };
        udb.storeNewUser(john, fb, o);
        // Clean up:
        udb.deleteUser("jj@epfl.ch", fb, o);
    }

    @Test
    public void setNewUserInvalid() {
        Calendar cal = Calendar.getInstance();
        cal.set(1945, 11, 21);
        User john = new User("John", "Jones", cal, "jj@mcu.us");
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(false));
            }
        };
        udb.storeNewUser(john, fb, o);
    }

    @Test
    public void fetchUserNotPresent() {
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertNull(udb.getFetchedUser());
            }
        };
        udb.fetchUser("jj@epfl.ch", fb, o);
    }

    @Test
    public void fetchUserPresent() {
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                User user = udb.getFetchedUser();
                Calendar cal = Calendar.getInstance();
                cal.set(1923, 12, 11);
                assertThat(user.getEmail(), is("mj@epfl.ch"));
                assertThat(user.getDateOfBirth(), is(cal));
                assertThat(user.getFirstName(), is("Micheal"));
                assertThat(user.getLastName(), is("Jaqueson"));
            }
        };
        udb.fetchUser("mj@epfl.ch", fb, o);
    }
*/
}
