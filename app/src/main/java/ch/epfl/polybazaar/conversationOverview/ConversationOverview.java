package ch.epfl.polybazaar.conversationOverview;

import ch.epfl.polybazaar.listing.Listing;

public class ConversationOverview {
    private Listing listing;
    
    ConversationOverview(String listingID){
        Listing.fetch(listingID).addOnSuccessListener(listing -> {
            this.listing = listing;
        });
    }

    String getSeller(){
        return listing.getUserEmail();
    }

    String getListingID(){
        return listing.getId();
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
        return listing.getUserEmail().equals(conv.listing.getUserEmail()) && listing.getId().equals(conv.listing.getId());
    }

    @Override
    public int hashCode() {
        return listing.getUserEmail().hashCode() + listing.getId().hashCode();
    }

}
