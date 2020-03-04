package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.Utilities.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Test
    public void createUserNormal() {
        User user = new User("nobody", "noone", "1939", "n.n@epfl.ch");
        assertThat(user.getFirstName(), is("nobody"));
        assertThat(user.getLastName(), is("noone"));
        assertThat(user.getDateOfBirth(), is("1939"));
        assertThat(user.getEmail(), is("n.n@epfl.ch"));
        assertThat(isValidUser(user), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail1() {
        User user = new User("nobody", "noone", "1939", "nn@epfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail2() {
        User user = new User("nobody", "noone", "1939", "me.help@efl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail3() {
        User user = new User("nobody", "noone", "1939", "me.h1lp@epfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidEmail4() {
        User user = new User("nobody", "noone", "1939", "me.helpepfl.ch");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidName() {
        User user = new User("no1ody", "noone", "1939", "me.help@efl.ch");
        assertThat(isValidUser(user), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserInvalidName2() {
        User user = new User("nobody", "n3one", "1939", "me.help@efl.ch");
        assertThat(isValidUser(user), is(false));
    }
}
