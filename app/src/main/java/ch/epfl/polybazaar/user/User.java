package ch.epfl.polybazaar.user;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

import static ch.epfl.polybazaar.Utilities.emailIsValid;
import static ch.epfl.polybazaar.Utilities.nameIsValid;


/**
 * If you change attributes of this class, also change its CallbackAdapter and Utilities
 */
public final class User extends Model {

    private String nickName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private static String COLLECTION = "users";

    private ArrayList<String> favorites;
    private ArrayList<String> ownListings = new ArrayList<>();

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
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

        firstName = capitalize(email.substring(0, email.indexOf(".")));
        lastName = capitalize(email.substring(email.indexOf(".")+1, email.indexOf("@")));
        phoneNumber = "";
    }

    /**
     * User contructor where names and phone number can be specified
     * @param nickName name displayed on listing
     * @param email email address, unique identifier (key)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param phoneNumber User's phone number
     */
    public User(String nickName, String email, String firstName, String lastName, String phoneNumber){
        this(nickName, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public ArrayList<String> getOwnListings() {
        return ownListings;
    }

    public void addOwnListing(String liteListingId) {
        ownListings.add(liteListingId);
    }

    public static Task<User> fetch(String email) {
        return ModelTransaction.fetch(COLLECTION, email, User.class);
    }

    public static Task<Void> editUser(User editedUser) {
        Task<Void> deleteUser = ModelTransaction.delete(COLLECTION, editedUser.getEmail()).addOnSuccessListener(aVoid -> editedUser.save());
        return Tasks.whenAll(deleteUser);
    }

    private String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}