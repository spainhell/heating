package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

public class RoomHeatingActivity extends AppCompatActivity {
    double newTemperature;
    EditText etTemper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_heating);

        Intent i = getIntent();

        CRoomHeatingParams heatingParams = (CRoomHeatingParams) i.getSerializableExtra("HeatingParams");

        etTemper = findViewById(R.id.editTemper);

        if (heatingParams != null) {
            Spinner roomsSpinner = findViewById(R.id.room_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, heatingParams.rooms);
            roomsSpinner.setAdapter(adapter);
            newTemperature = heatingParams.setTemperature;
            etTemper.setText(String.format(Locale.ENGLISH, "%.1f", newTemperature));
        }
    }

    public void btnMinusOnClick(View view) {
        newTemperature -= 0.5;
        etTemper.setText(String.format(Locale.ENGLISH, "%.1f", newTemperature));
    }

    public void btnPlusOnClick(View view) {
        newTemperature += 0.5;
        etTemper.setText(String.format(Locale.ENGLISH, "%.1f", newTemperature));
    }
}
