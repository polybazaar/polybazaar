package ch.epfl.polybazaar;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.user.User;

public abstract class Utilities {

    public static Map<String, Object> getMap(Object o) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (o instanceof Listing) {
            result.put("description", ((Listing)o).getDescription());
            result.put("price", ((Listing)o).getPrice());
            result.put("title", ((Listing)o).getTitle());
            result.put("userEmail", ((Listing)o).getUserEmail());
        } else if (o instanceof LiteListing) {
            result.put("listingID", ((LiteListing)o).getListingID());
            result.put("price", ((LiteListing)o).getPrice());
            result.put("title", ((LiteListing)o).getTitle());
        } else if (o instanceof User) {
            result.put("email", ((User)o).getEmail());
            result.put("nickName", ((User)o).getNickName());
        } else {
            return null;
        }
        return result;
    }

    // TODO: can be adjusted
    public static boolean nameIsValid(String name) {
        //return (name.matches("[a-zA-Z]+"));
        return true;
    }

    public static boolean emailIsValid(String email) {
        return (email.matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"));
    }

    public static boolean isValidUser(User user) {
        if (user != null
        && nameIsValid(user.getNickName())
        && emailIsValid(user.getEmail())) {
            return true;
        }
        return false;
    }

    public static Object getOrDefaultObj(Map<String, Object> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

    public static Map<String, Object> getOrDefaultMap(Map<String, Map<String, Object>> map, String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

}
