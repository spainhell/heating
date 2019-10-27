package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

public class ParametersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        Intent i = getIntent();
        CSystemParams systemParams = (CSystemParams) i.getSerializableExtra("SystemParams");

        setComponents(systemParams);
    }

    protected void setComponents(CSystemParams systemParams) {
        Switch sw_antifreeze = findViewById(R.id.switch_antifreeze2);
        Switch sw_heating = findViewById(R.id.switch_heating2);
        Switch sw_hotwater = findViewById(R.id.switch_hot_water2);
        EditText edit_delta = findViewById(R.id.editDelta);

        sw_antifreeze.setChecked(systemParams.Antifreeze);
        sw_heating.setChecked(systemParams.Heating);
        sw_hotwater.setChecked(systemParams.HotWater);
        edit_delta.setText(String.valueOf(systemParams.Delta));

    }
}
