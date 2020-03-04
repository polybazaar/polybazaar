package ch.epfl.polybazaar;

import java.util.Calendar;

public abstract class Utilities {

    public static boolean nameIsValid(String name) {
        return (name.matches("(\\w+)"));
    }

    public static boolean dateIsValid(Calendar date) {
        // TODO: implement
        return  true;
    }

    public static boolean emailIsValid(String email) {
        return (email.matches("(\\w+)"+"."+"(\\w+)"+"@epfl.ch"));
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
