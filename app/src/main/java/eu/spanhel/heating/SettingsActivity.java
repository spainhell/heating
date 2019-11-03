package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        CSetting appSettings = (CSetting) intent.getSerializableExtra("AppSettings");

        if (appSettings != null) {
            EditText etUrl = findViewById(R.id.edit_url);
            etUrl.setText(appSettings.url);
            EditText etUsername = findViewById(R.id.edit_login);
            etUsername.setText(appSettings.username);
            EditText etPassword = findViewById(R.id.edit_password);
            etPassword.setText(appSettings.password);
            //EditText etDistribUrl = findViewById(R.id.edit_url);
        }


    }

    public void btnSaveOnClick(View view) {
        Intent data = new Intent();
        CSetting newSettings = new CSetting();
        EditText etUrl = findViewById(R.id.edit_url);
        newSettings.url = etUrl.getText().toString();
        EditText etUsername = findViewById(R.id.edit_login);
        newSettings.username = etUsername.getText().toString();
        EditText etPassword = findViewById(R.id.edit_password);
        newSettings.password = etPassword.getText().toString();

        data.putExtra("newSettings", newSettings);

        setResult(RESULT_OK, data);
        finish();
    }

    public void btnBackClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
