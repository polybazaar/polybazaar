package ch.epfl.polybazaar.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.polybazaar.R;

public class SubmitOffer extends AppCompatActivity {

    private EditText inputOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_offer);
        inputOffer = findViewById(R.id.offer);
    }

    public void submitOffer(View view) {
        if (!String.valueOf(inputOffer.getText()).isEmpty()) {
            double offer = Double.parseDouble(String.valueOf(inputOffer.getText()));
            // TODO : Send offer
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.offer_invalid, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelOfferMaking(View view) {
        finish();
    }
}
