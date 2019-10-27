package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnChangeRoomsHeatingClicked(View view) {
        Intent intent = new Intent(this, RoomHeatingActivity.class);

        CRoomHeatingParams heatingParams = new CRoomHeatingParams();
        heatingParams.rooms = new String[]{"Loznice", "Kuchyn", "Obyvak", "Kotelna", "Pokojik"};
        heatingParams.selectedRoom = heatingParams.rooms[0];
        heatingParams.setTemperature = 20.0;

        intent.putExtra("HeatingParams", heatingParams);
        startActivity(intent);
    }

    public void btnChangeParamsClicked(View view) {
        Intent intent = new Intent(this, ParametersActivity.class);

        CSystemParams systemParams = new CSystemParams();
        systemParams.Antifreeze = false;
        systemParams.Heating = false;
        systemParams.HotWater = true;
        systemParams.Delta = 0.5;

        intent.putExtra("SystemParams", systemParams);
        startActivity(intent);
    }
}
