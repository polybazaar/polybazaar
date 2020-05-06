package ch.epfl.polybazaar.notifications;

/**
 * This class HAS TO be named exactly "Data" in order to be accepted by FCM
 */
public class Data {
    private String sender;
    private String body;
    private String title;
    private String to;
    private String listingID;

    public static final String SENDER = "sender";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String TO = "to";
    public static final String LISTINGID = "listingID";

    public Data(String sender, String body, String title, String to, String listingID) {
        this.sender = sender;
        this.body = body;
        this.title = title;
        this.to = to;
        this.listingID = listingID;
    }
}
