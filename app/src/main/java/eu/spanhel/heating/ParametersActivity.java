package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class ParametersActivity extends AppCompatActivity {
    private Switch sw_antifreeze;
    private Switch sw_heating;
    private Switch sw_hotwater;
    private EditText edit_delta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        Intent i = getIntent();
        CSystemParams systemParams = (CSystemParams) i.getSerializableExtra("SystemParams");

        setComponents(systemParams);
    }

    protected void setComponents(CSystemParams systemParams) {
        if (systemParams == null) return;

        sw_antifreeze = findViewById(R.id.switch_antifreeze2);
        sw_heating = findViewById(R.id.switch_heating2);
        sw_hotwater = findViewById(R.id.switch_hot_water2);
        edit_delta = findViewById(R.id.editDelta);

        sw_antifreeze.setChecked(systemParams.Antifreeze);
        sw_heating.setChecked(systemParams.Heating);
        sw_hotwater.setChecked(systemParams.HotWater);
        edit_delta.setText(String.valueOf(systemParams.Delta));

    }

    public void btnSaveOnClick(View view) {
        Intent data = new Intent();

        CSystemParams newParams = new CSystemParams();
        newParams.Antifreeze = sw_antifreeze.isChecked();
        newParams.Heating = sw_heating.isChecked();
        newParams.HotWater = sw_hotwater.isChecked();
        newParams.Delta = Double.parseDouble(edit_delta.getText().toString());

        data.putExtra("newParams", newParams);
        setResult(RESULT_OK, data);
        finish();
    }

    public void btnBackClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
