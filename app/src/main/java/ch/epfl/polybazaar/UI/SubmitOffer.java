package ch.epfl.polybazaar.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_ACCEPTED;
import static ch.epfl.polybazaar.chat.ChatMessage.OFFER_PROCESSED;
import static ch.epfl.polybazaar.listing.Listing.SOLD;
import static java.util.UUID.randomUUID;

public class SubmitOffer extends AppCompatActivity {

    public static final String LISTING = "LISTING";
    private EditText inputOffer;
    private Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_offer);
        inputOffer = findViewById(R.id.offer);
        listing = (Listing)getIntent().getSerializableExtra(LISTING);
    }

    public void submitOffer(View view) {
        if (!String.valueOf(inputOffer.getText()).isEmpty()) {
            double offer = Double.parseDouble(inputOffer.getText().toString());
            // TODO : Send offer
            sendOffer(offer, listing, SubmitOffer.this);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.offer_invalid, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelOfferMaking(View view) {
        finish();
    }

    /**
     * sends an offer
     * @param offer the offer amount
     */
    public static void sendOffer(Double offer, Listing listing, Activity activity) {
        String senderEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();
        ChatMessage message = new ChatMessage(senderEmail, listing.getUserEmail(),
                listing.getId(),
                ChatMessage.OFFER_MADE + offer.toString(),
                new Timestamp(new Date(System.currentTimeMillis())));
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);
        message.save().addOnSuccessListener(aVoid ->
                Toast.makeText(activity.getApplicationContext(), R.string.offer_sent, Toast.LENGTH_LONG).show());
    }

    /**
     * Accepts or refuses the offer
     * @param offer the offer amount
     * @param receivedOfferMessage the message received by the seller asking to accept or refuse the offer
     * @param offerStatus OFFER_ACCEPTED or OFFER_REFUSED
     */
    public static void processOffer(Double offer, ChatMessage receivedOfferMessage, String offerStatus) {
        String listingID = receivedOfferMessage.getListingID();
        String sellerEmail = receivedOfferMessage.getReceiver();
        String buyerEmail = receivedOfferMessage.getSender();
        String messageContent = OFFER_PROCESSED + offerStatus + offer.toString();
        ChatMessage message = new ChatMessage(sellerEmail, buyerEmail,
                listingID,
                messageContent,
                new Timestamp(new Date(System.currentTimeMillis())));
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);
        message.save().addOnSuccessListener(aVoid -> receivedOfferMessage.delete());
        // disable offer making and put sold banner
        if (offerStatus.equals(OFFER_ACCEPTED)) {
            Listing.updateField(Listing.LISTING_ACTIVE, message.getListingID(), false);
            Listing.updateField(Listing.PRICE, message.getListingID(), Listing.SOLD);
            Map<String, Object> map =new HashMap<>();
            map.put(LiteListing.PRICE, LiteListing.SOLD);
            LiteListing.updateMultipleFields(message.getListingID(), map);
        }
    }
}
