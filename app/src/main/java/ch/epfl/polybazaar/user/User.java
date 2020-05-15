package ch.epfl.polybazaar.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import ch.epfl.polybazaar.database.SimpleField;
import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

import static ch.epfl.polybazaar.Utilities.emailIsValid;
import static ch.epfl.polybazaar.Utilities.nameIsValid;


/**
 * If you change attributes of this class, also change its CallbackAdapter and Utilities
 */
public final class User extends Model {

    public static final String NICK_NAME = "nickName";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PROFILE_PICTURE_REF = "profilePictureRef";
    public static final String OWN_LISTINGS = "ownListings";
    public static final String FAVORITES = "favorites";
    public final static String TOKEN ="token";

    private final SimpleField<String> nickName = new SimpleField<>(NICK_NAME);
    private final SimpleField<String> email = new SimpleField<>(EMAIL);
    private final SimpleField<String> firstName = new SimpleField<>(FIRST_NAME);
    private final SimpleField<String> lastName = new SimpleField<>(LAST_NAME);
    private final SimpleField<String> phoneNumber = new SimpleField<>(PHONE_NUMBER);
    private final SimpleField<String> profilePictureRef = new SimpleField<>(PROFILE_PICTURE_REF);
    private final SimpleField<ArrayList<String>> ownListings = new SimpleField<>(OWN_LISTINGS, new ArrayList<>());
    private final SimpleField<ArrayList<String>> favorites = new SimpleField<>(FAVORITES, new ArrayList<>());
    private final SimpleField<String> token = new SimpleField<>(TOKEN);

    private final static String COLLECTION = "users";
    public final static String NO_PROFILE_PICTURE = "no_picture";



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

    public String getToken() {
        return token.get();
    }

    public String getProfilePictureRef(){
        if (profilePictureRef.get() != null) {
            return profilePictureRef.get();
        } else {
            return NO_PROFILE_PICTURE;
        }
    }

    // no-argument constructor so that instances can be created by ModelTransaction
    public User() {
        registerFields(nickName, email, firstName, lastName, phoneNumber, profilePictureRef, ownListings, favorites, token);
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
        profilePictureRef.set(NO_PROFILE_PICTURE);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> token.set(instanceIdResult.getToken()));
    }

    /**
     * User contructor where names and phone number can be specified
     * @param nickName name displayed on listing
     * @param email email address, unique identifier (key)
     * @param firstName User's first name
     * @param lastName User's last name
     * @param phoneNumber User's phone number
     * @param profilePictureRef StringImage that is the user's profile Picture
     */
    public User(String nickName, String email, String firstName, String lastName, String phoneNumber,
                String profilePictureRef, ArrayList<String> ownListings, ArrayList<String> favorites){
        this(nickName, email);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.phoneNumber.set(phoneNumber);
        this.profilePictureRef.set(profilePictureRef);
        this.ownListings.set(ownListings);
        this.favorites.set(favorites);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> token.set(instanceIdResult.getToken()));
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
     * @param listingID listing to add
     */
    public void addFavorite(String listingID) {
        favorites.get().add(listingID);
    }

    /**
     * Removes a listing from the user's favorites
     * @param listingID listing to remove
     */
    public void removeFavorite(String listingID) {
        favorites.get().remove(listingID);
    }

    public static <T> Task<Void> updateField(String field, String id, T updatedValue) {

        return ModelTransaction.updateField(COLLECTION, id, field, updatedValue);
    }


    public static Task<User> fetch(String email) {
        return ModelTransaction.fetch(COLLECTION, email, User.class);
    }

    private String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}