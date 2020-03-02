package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDatabaseTest {
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private UserDatabase udb = UserDatabase.getInstance();
    private Observer o = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

        }
    };

   // @Before
    @Test
    public void databaseInit() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User micheal = new User("Micheal", "Jaqueson", cal, "mj@epfl.ch");
        cal.set(2001, 03, 30);
        User william = new User("William", "Shakespeare", cal, "ws@epfl.ch");
        udb.storeNewUser(micheal, fb, o);
        udb.storeNewUser(william, fb, o);
    }
/*
    //@After
    @Test
    public void databaseClean() {
        assertThat(UserDatabase.deleteUser("mj@epfl.ch", fb), is(true));
        assertThat(UserDatabase.deleteUser("ws@epfl.ch", fb), is(true));
    }
*/
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
