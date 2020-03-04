package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.userdatabase.User;
import ch.epfl.polybazaar.userdatabase.UserDatabase;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {

    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private UserDatabase udb = new UserDatabase();

    @Before
    public void databaseInit() throws InterruptedException, ExecutionException {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User micheal = new User("Micheal", "Jaqueson", cal, "mj@epfl.ch");
        cal.set(2001, 03, 30);
        User william = new User("William", "Shakespeare", cal, "ws@epfl.ch");
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(true));
                //assertThat(udb.isSuccess(), is(false));    // to delete
            }
        };
        udb.storeNewUser(micheal, fb, o);
        udb.storeNewUser(william, fb, o);
    }

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

}
