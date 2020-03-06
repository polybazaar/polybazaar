package ch.epfl.polybazaar.user;

import java.util.Calendar;

import static ch.epfl.polybazaar.Utilities.*;

public class User {

    private String nickName;
    private String email;

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
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
            throw new IllegalArgumentException("first name has invalid format");
        }
        if (emailIsValid(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
    }


}