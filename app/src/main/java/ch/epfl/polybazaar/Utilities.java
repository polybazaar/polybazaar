package ch.epfl.polybazaar;

import java.util.Calendar;

public abstract class Utilities {

    public static boolean nameIsValid(String name) {
        // TODO : implement
        return  true;
    }

    public static boolean dateIsValid(Calendar date) {
        // TODO : implement
        return  true;
    }

    public static boolean emailIsValid(String email) {
        // TODO : implement, check that ends with "@epfl.ch"
        return  true;
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
