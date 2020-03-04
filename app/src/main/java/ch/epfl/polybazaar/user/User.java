package ch.epfl.polybazaar.user;

import java.util.Calendar;

import static ch.epfl.polybazaar.Utilities.*;

public class User {

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    /**
     * User Constructor
     * @param firstName first name
     * @param lastName last name
     * @param dateOfBirth date of birth
     * @param email email address, unique identifier (key)
     * @throws IllegalArgumentException
     */
    public User(String firstName, String lastName, String dateOfBirth, String email) throws IllegalArgumentException {
        if (nameIsValid(firstName)) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("first name has invalid format");
        }
        if (nameIsValid(lastName)) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("last name has invalid format");
        }
        if (dateIsValid(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        } else {
            throw new IllegalArgumentException("date of birth has invalid format");
        }
        if (emailIsValid(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
    }


}