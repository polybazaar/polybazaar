package ch.epfl.polybazaar.user;

import android.util.Log;

import static ch.epfl.polybazaar.Utilities.*;


/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 * CAUTION : the document ID of a user does not contain @epfl.ch and can be obtained using getID()
 */
public class User {

    private String nickName;
    private String email;

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email + "@epfl.ch";
    }

    public String getID() {
        return email;
    }

    /**
     * User Constructor
     * @param nickName name displayed on listing
     * @param email email address, unique identifier (key)
     * @throws IllegalArgumentException
     */
    public User(String nickName, String email) throws IllegalArgumentException {
        if (nameIsValid(nickName)) {
            this.nickName = nickName;
        } else {
            throw new IllegalArgumentException("nickName has invalid format");
        }
        if (emailIsValid(email)) {
            String newEmail = email.replace("@epfl.ch", "");
            this.email = newEmail;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
    }


}