package ch.epfl.polybazaar.utilities;

import android.content.Context;

import java.util.regex.Pattern;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.user.User;

public abstract class InputValidity {

    public final static String ERROR = "error";

    /**
     * Decides if the given name is valid. A name is considered valid if and only if it is written with letters
     * @param name the name to decide the validity of
     * @return true if the name is valid, false otherwise
     */
    public static boolean nameIsValid(String name) {
        return (name.matches("[a-zA-Z]+"));
    }

    /**
     * Decides if the given email is a valid EPFL one. It is valid if and only if it starts with (char)*.(char)* and ends with @epfl.ch
     * @param email the email to decide the validity of
     * @return true if the email is valid, false otherwise
     */
    public static boolean emailIsValid(String email) {
        return (email.matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"));
    }



    /**
     * Determines if a given password is valid. It is valid if its length has the required value and it has at least one lower case letter, one upper case letter and one digit
     * @param password the password to check
     * @param context the application context
     * @return the error message. If the password is valid, returns the empty string
     */
    public static String passwordValidity(String password, Context context) {
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        String errorString = "";

        if(password.length() < context.getResources().getInteger(R.integer.min_password_length)){
            errorString += context.getString(R.string.password_too_short)+" "+context.getResources().getInteger(R.integer.min_password_length) + " "+context.getString(R.string.char_long)+"\n";
        }

        if(!lowerCase.matcher(password).find()){
            errorString += context.getString(R.string.password_lowercase)+"\n";
        }
        if(!upperCase.matcher(password).find()){
            errorString += context.getString(R.string.password_uppercase)+"\n";
        }
        if(!digit.matcher(password).find()){
            errorString += context.getString(R.string.password_digit);
        }
        return errorString;
    }

    /**
     * Determines if a nickname is valid. It is valid if it has the required length and contains at least one letter. Note: if the context is null, then hard-coded value are used
     * @param nickname the nickname to check
     * @param context the application context
     * @return the error message. If the nickname is value, return the empty string
     */
    public static String nicknameValidity(String nickname, Context context) {
        String errorString = "";
        if(context != null){
            if(nickname.length() < context.getResources().getInteger(R.integer.min_nickname_length)){
                errorString += context.getString(R.string.nickname_too_short) + " "+context.getResources().getInteger(R.integer.min_nickname_length)+" "+context.getString(R.string.char_long)+"\n";
            }
        }
        else{
            if(nickname.length() < 2){
                errorString += ERROR;
            }
        }
        return errorString;
    }

    /**
     * Determines if a user is valid based on the validity of its nickname and its email
     * @param user the user to check
     * @return true if the user is valid, false otherwise
     */
    public static boolean isValidUser(User user) {
        if (user != null
                && nameIsValid(user.getNickName())
                && emailIsValid(user.getEmail())) {
            return true;
        }
        return false;
    }
}
