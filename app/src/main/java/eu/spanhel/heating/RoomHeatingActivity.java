package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RoomHeatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_heating);

        Intent i = getIntent();

        CRoomHeatingParams heatingParams = (CRoomHeatingParams) i.getSerializableExtra("HeatingParams");


        if (heatingParams != null) {
            Spinner roomsSpinner = findViewById(R.id.room_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, heatingParams.rooms);
            roomsSpinner.setAdapter(adapter);
        }

    }
}
