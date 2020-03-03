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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private UserDatabase udb = new UserDatabase();

    private void runLong() {
        double x = 1.0;
        for (long i = 0 ; i <999999999 ; ++i) {
            x = x *1.000001*1.0000002;
        }
    }

   // @Before
    @Test
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
                assertThat(udb.isSuccess(), is(false));    // to delete
            }
        };
        udb.storeNewUser(micheal, fb, o);
        runLong();
        runLong();
        runLong();
        udb.storeNewUser(william, fb, o);
        runLong();
        runLong();
        runLong();
    }

    //@After
    @Test
    public void databaseClean() {
        Observer o = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assertThat(udb.isSuccess(), is(true));
                assertThat(udb.isSuccess(), is(false));  // to delete
            }
        };
        udb.deleteUser("mj@epfl.ch", fb, o);
        runLong();
        runLong();
        runLong();
        udb.deleteUser("ws@epfl.ch", fb, o);
        runLong();
        runLong();
        runLong();
    }

/*
    @Test
    public void setNewUserAlreadyPresent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1913, 01, 11);
        User john = new User("John", "Jones", cal, "mj@epfl.ch");
        assertThat(UserDatabase.storeNewUser(john, fb), is(false));
    }

    @Test
    public void setNewUserNotPresent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1945, 11, 21);
        User john = new User("John", "Jones", cal, "jj@epfl.ch");
        assertThat(UserDatabase.storeNewUser(john, fb), is(true));
        // Clean up:
        assertThat(UserDatabase.deleteUser("jj@epfl.ch", fb), is(true));
    }

    @Test
    public void setNewUserInvalid() {
        Calendar cal = Calendar.getInstance();
        cal.set(1945, 11, 21);
        User john = new User("John", "Jones", cal, "jj@mcu.us");
        assertThat(UserDatabase.storeNewUser(john, fb), is(false));
    }

    @Test
    public void fetchUserNotPresent() {
        assertNull(UserDatabase.fetchUser("jj@epfl.ch", fb));
    }

    @Test
    public void fetchUserPresent() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = UserDatabase.fetchUser("mj@epfl.ch", fb);
        assertThat(user.getEmail(), is("mj@epfl.ch"));
        assertThat(user.getDateOfBirth(), is(cal));
        assertThat(user.getFirstName(), is("Micheal"));
        assertThat(user.getLastName(), is("Jaqueson"));
    }
    */
}
