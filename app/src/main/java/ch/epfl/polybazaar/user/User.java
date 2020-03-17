package ch.epfl.polybazaar.user;

import static ch.epfl.polybazaar.Utilities.*;


/**
 * If you attributes of this class, also change its CallbackAdapter and MockDataSnapshot
 */
public class User {

    private String nickName;
    private String email;

    public String getNickName() {
        if (nickName == null) return null;
        return nickName;
    }

    public String getEmail() {
        if (email == null) return null;
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
            this.email = email;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
    }


}