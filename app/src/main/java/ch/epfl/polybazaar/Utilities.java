package ch.epfl.polybazaar;

import java.util.Calendar;

import ch.epfl.polybazaar.user.User;

public abstract class Utilities {

    public static boolean nameIsValid(String name) {
        return (name.matches("[a-zA-Z]+"));
    }

    public static boolean dateIsValid(String date) {
        // TODO: implement
        return  true;
    }

    public static boolean emailIsValid(String email) {
        return (email.matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"));
    }

    public static boolean isValidUser(User user) {
        if (user != null
        && nameIsValid(user.getFirstName())
        && nameIsValid(user.getLastName())
        && dateIsValid(user.getDateOfBirth())
        && emailIsValid(user.getEmail())) {
            return true;
        }
        return false;
    }

}
