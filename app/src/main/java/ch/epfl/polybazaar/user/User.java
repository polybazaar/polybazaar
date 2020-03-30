package ch.epfl.polybazaar.user;

import com.google.android.gms.tasks.Task;

import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

import static ch.epfl.polybazaar.Utilities.*;


/**
 * If you attributes of this class, also change its CallbackAdapter and Utilities
 */
public final class User extends Model {

    private String nickName;
    private String email;

    private static String COLLECTION = "users";

    private List<String> favorites;

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    private User() {}

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

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return email;
    }

    @Override
    public void setId(String id) {
        this.email = id;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public static Task<User> retrieve(String email) {
        return ModelTransaction.fetch(COLLECTION, email, User.class);
    }
}