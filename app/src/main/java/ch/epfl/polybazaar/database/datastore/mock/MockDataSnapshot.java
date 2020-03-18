package ch.epfl.polybazaar.database.datastore.mock;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.Utilities.getOrDefaultObj;

public class MockDataSnapshot implements DataSnapshot {

    private Map<String, Object> data;

    private String ID;

    private Map<String, Object> getMap(Object o) {
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

    public MockDataSnapshot(String ID, Object data) {
        this.ID = ID;
        this.data = getMap(data);
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Object get(String field) {
        return getOrDefaultObj(data, field);
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public String getId() {
        return ID;
    }
}
