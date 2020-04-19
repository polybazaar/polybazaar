package ch.epfl.polybazaar.user;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.polybazaar.database.Field;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.Utilities.emailIsValid;
import static ch.epfl.polybazaar.Utilities.nameIsValid;


/**
 * If you change attributes of this class, also change its CallbackAdapter and Utilities
 */
public final class User extends Model {
    private final Field<String> nickName = new Field<>("nickName");
    private final Field<String> email = new Field<>("email");
    private final Field<String> firstName = new Field<>("firstName");
    private final Field<String> lastName = new Field<>("lastName");
    private final Field<String> phoneNumber = new Field<>("phoneNumber");
    private final Field<ArrayList<String>> ownListings = new Field<>("ownListings", new ArrayList<>());
    private final Field<ArrayList<String>> favorites = new Field<>("favorites", new ArrayList<>());

    private final static String COLLECTION = "users";

    public String getNickName() {
        return nickName.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getFirstName(){
        return firstName.get();
    }

    public String getLastName(){
        return lastName.get();
    }

    public String getPhoneNumber(){
        return phoneNumber.get();
    }

    public User() {
        registerFields(nickName, email, firstName, lastName, phoneNumber, ownListings, favorites);
    }

    /**
     * User Constructor
     * @param nickName name displayed on listing
     * @param email email address, unique identifier (key)
     * @throws IllegalArgumentException
     */
    public User(String nickName, String email) {
        this();
        if (nameIsValid(nickName)) {
            this.nickName.set(nickName);
        } else {
            throw new IllegalArgumentException("nickName has invalid format");
        }
        if (emailIsValid(email)) {
            this.email.set(email);
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }

        firstName.set(capitalize(email.substring(0, email.indexOf("."))));
        lastName.set(capitalize(email.substring(email.indexOf(".")+1, email.indexOf("@"))));
        phoneNumber.set("");
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
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.phoneNumber.set(phoneNumber);
    }

    @Override
    public String collectionName() {
        return COLLECTION;
    }

    @Override
    public String getId() {
        return email.get();
    }

    @Override
    public void setId(String id) {
        this.email.get();
    }

    public ArrayList<String> getFavorites() {
        return favorites.get();
    }

    public ArrayList<String> getOwnListings() {
        return ownListings.get();
    }

    public void addOwnListing(String liteListingId) {
        ownListings.get().add(liteListingId);
    }

    public void deleteOwnListing(String liteListingId) {
        ownListings.get().remove(liteListingId);
    }

    /**
     * Adds a listing to the user's favorites
     * @param listing listing to add
     */
    public void addFavorite(Listing listing) {
        favorites.get().add(listing.getId());
    }

    /**
     * Removes a listing from the user's favorites
     * @param listing listing to remove
     */
    public void removeFavorite(Listing listing) {
        favorites.get().remove(listing.getId());
    }

    public static Task<User> fetch(String email) {
        return ModelTransaction.fetch(COLLECTION, email, User.class);
    }

    private String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}