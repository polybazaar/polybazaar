package ch.epfl.polybazaar.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.util.Date;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

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
                ChatMessage.OFFER + offer.toString(),
                new Timestamp(new Date(System.currentTimeMillis())));
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);
        message.save().addOnSuccessListener(aVoid ->
                Toast.makeText(activity.getApplicationContext(), R.string.offer_sent, Toast.LENGTH_LONG).show());
    }
}
