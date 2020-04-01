package ch.epfl.polybazaar.widgets.permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.epfl.polybazaar.R;

public class PermissionsTestActivity extends AppCompatActivity {

    private Button button;
    private TextView text;
    private PermissionRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_permissions);
        button = findViewById(R.id.askPermission);
        text = findViewById(R.id.permissionText);
        button.setOnClickListener(view -> askPermission());
    }

    private void askPermission() {
        request = new PermissionRequest(this, "ACCESS_FINE_LOCATION", "I need this", "WHY", result -> {
            if (result) text.setText("DONE");
            else text.setText("NOT HAPPY");
        });
        request.assertPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        request.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
