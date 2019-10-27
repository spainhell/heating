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
        String[] rooms = new String[]{"Ložnice", "Kuchyň", "Obývák"};
        intent.putExtra("RoomsList", rooms);
        startActivity(intent);
    }
}
