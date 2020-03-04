package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import static ch.epfl.polybazaar.Utilities.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Test
    public void createUserNormal() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "noone", cal, "n.n@epfl.ch");
        assertThat(user.getFirstName(), is("nobody"));
        assertThat(user.getLastName(), is("noone"));
        assertThat(user.getDateOfBirth(), is(cal));
        assertThat(user.getEmail(), is("nn@epfl.ch"));
        assertThat(isValidUser(user), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail1() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "noone", cal, "nn@epfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail2() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "noone", cal, "me.help@efl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail3() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "noone", cal, "me.h1lp@epfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail4() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "noone", cal, "me.helpepfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidName() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("no1ody", "noone", cal, "me.help@efl.ch");
        assertThat(isValidUser(user), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidName2() {
        Calendar cal = Calendar.getInstance();
        cal.set(1923, 12, 11);
        User user = new User("nobody", "n3one", cal, "me.help@efl.ch");
        assertThat(isValidUser(user), is(false));
    }
}
