package ch.epfl.polybazaar;

import java.util.Map;

import ch.epfl.polybazaar.user.User;

public abstract class Utilities {

    public static boolean nameIsValid(String name) {
        return (name.matches("[a-zA-Z]+"));
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
