package ch.epfl.polybazaar.login;

public class AuthenticatorFactory {
    private static Authenticator dependency = FirebaseAuthenticator.getInstance();

    public static Authenticator getDependency() {
        return dependency;
    }

    public static void setDependency(Authenticator authenticator) {
        dependency = authenticator;
    }
}
