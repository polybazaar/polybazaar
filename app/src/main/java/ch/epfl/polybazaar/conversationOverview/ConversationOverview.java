package ch.epfl.polybazaar.conversationOverview;

public class ConversationOverview {
    private String otherUser;
    private String listingID;
    
    ConversationOverview(String otherUser, String listingName){
        this.otherUser = otherUser;
        this.listingID = listingName;
    }

    String getOtherUser(){
        return otherUser;
    }

    String getListingID(){
        return listingID;
    }

    @Override
    public boolean equals(Object other){
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;
        ConversationOverview conv = (ConversationOverview) other;
        return otherUser.equals(conv.otherUser) && listingID.equals(conv.listingID);
    }

    @Override
    public int hashCode() {
        return listingID.hashCode() + otherUser.hashCode();
    }

}
