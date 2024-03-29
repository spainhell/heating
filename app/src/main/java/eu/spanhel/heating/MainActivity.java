package eu.spanhel.heating;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {
    private JSONArray temperArray;
    private CSystemParams sysParams;
    private CRoomHeatingParams heatingParams;
    private CSetting appSettings;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!loadConfiguration()) {
            onClickMenuSetting(null);
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("AppSettings", appSettings);
            startActivityForResult(intent, 9);
        }

        if (appSettings.url != null) {
            showProgressBar(true);
            getConfigFromApi();
            getTemperaturesFromApi();
        }
    }

    protected Boolean loadConfiguration() {
        pref = getApplicationContext().getSharedPreferences("Settings", 0);
        appSettings = new CSetting();
        appSettings.url = pref.getString("url", null);
        appSettings.username = pref.getString("username", null);
        appSettings.password = pref.getString("password", null);
        appSettings.url_distrib = pref.getString("url_distrib", null);
        return appSettings.url != null;
    }

    protected void saveConfiguration() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("url", appSettings.url);
        editor.putString("username", appSettings.username);
        editor.putString("password", appSettings.password);
        //editor.apply();
        editor.commit();
    }

    protected void getConfigFromApi() {
        // showProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://" + appSettings.url + "/api/config";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                try {
                    Button btnEditHeating = findViewById(R.id.btn_edit_heating);
                    btnEditHeating.setEnabled(true);

                    // z API prijde JSON objekt
                    JSONObject configJson = new JSONObject(response);
                    sysParams = new CSystemParams();
                    sysParams.Antifreeze = configJson.getString("antifreeze").equals("true");
                    sysParams.Heating = configJson.getString("heating").equals("true");
                    sysParams.HotWater = configJson.getString("hotwater").equals("true");
                    sysParams.Delta = Double.parseDouble(configJson.getString("delta"));

                    heatingParams = new CRoomHeatingParams();
                    heatingParams.selectedRoom = configJson.getString("sensor");
                    heatingParams.setTemperature = Double.parseDouble(configJson.getString("temperature"));

                    setSwitches();

                } catch (JSONException e) {
                    TextView tvDateTime = findViewById(R.id.label_datetime);
                    tvDateTime.setText("ERROR");
                    e.printStackTrace();
                }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //temperatures[0] = "That didn't work!";
        }
    });

        queue.add(stringRequest);
    }

    protected void setSwitches() {
        Switch swHeating = findViewById(R.id.switch_heating);
        Switch swHotWater = findViewById(R.id.switch_hot_water);

        swHeating.setChecked(sysParams.Heating);
        swHotWater.setChecked(sysParams.HotWater);
    }

    protected void getTemperaturesFromApi() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://" + appSettings.url + "/api/temperatures";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Button btnEditRooms = findViewById(R.id.btn_edit_rooms);
                            btnEditRooms.setEnabled(true);
                            // z API prijde pole teplot
                            temperArray = new JSONArray(response);

                            TextView tvDateTime = findViewById(R.id.label_datetime);
                            tvDateTime.setText(String.format("%s", temperArray.getJSONObject(0).get("time")));

                            addRoomsToTable();
                            showProgressBar(false);

                        } catch (JSONException e) {
                            showProgressBar(false);
                            TextView tvDateTime = findViewById(R.id.label_datetime);
                            tvDateTime.setText("ERROR");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //temperatures[0] = "That didn't work!";
            }
        });

        queue.add(stringRequest);
    }

    protected void addRoomsToTable() throws JSONException {
        TableLayout table = findViewById(R.id.tableLayout);

        table.removeAllViews();

        for (int i = 0; i < temperArray.length(); i++) {
            JSONObject jo = temperArray.getJSONObject(i);
            String place = jo.getString("place");
            Double temperature = jo.getDouble("temperature");

            // vlozime mistnost mezi mistnosti
            heatingParams.rooms.add(place);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView tvRoom = new TextView(this);
            tvRoom.setText(place);
            tvRoom.setPadding(3, 3, 3, 3);
            TextView tvTemperature = new TextView(this);
            tvTemperature.setGravity(Gravity.END);
            tvTemperature.setPadding(3, 3, 3, 3);
            tvTemperature.setText(String.format("%s °C", temperature));

            if (temperature < 10) {
                tvTemperature.setTextColor(Color.parseColor("#0000ff"));
            }
            else if (temperature > 22) {
                tvTemperature.setTextColor(Color.parseColor("#ff0000"));
            }
            else {
                tvTemperature.setTextColor(Color.parseColor("#006400"));
            }

            row.addView(tvRoom);
            row.addView(tvTemperature);

            if (place.equals(heatingParams.selectedRoom)) {
                tvRoom.setText(String.format("%s (pož. %s °C)", place, heatingParams.setTemperature));

                ShapeDrawable sd = new ShapeDrawable();
                sd.setShape(new RectShape());
                sd.getPaint().setColor(Color.BLACK);
                sd.getPaint().setStrokeWidth(2f);
                sd.getPaint().setStyle(Paint.Style.STROKE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    row.setBackground(sd);
                }
            }

            table.addView(row, i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        // návrat z ActivityRoomHeating
        if (requestCode == 0) {
            if (data != null) {
                showProgressBar(true);
                String room = data.getStringExtra("room");
                Double temperature = data.getDoubleExtra("temperature", 0.0);
                setNewRoomOrTemperature(room, temperature);
            }
        }
        // návrat z ActivityParameters
        else if (requestCode == 1) {
            if (data != null) {
                CSystemParams newParams = (CSystemParams) data.getSerializableExtra("newParams");
                boolean paramsChanged = false;
                if (newParams != null) {
                    if (newParams.Heating != sysParams.Heating) { paramsChanged = true; }
                    if (newParams.Antifreeze != sysParams.Antifreeze) { paramsChanged = true; }
                    if (newParams.HotWater != sysParams.HotWater) { paramsChanged = true; }
                    if (newParams.Delta != sysParams.Delta) { paramsChanged = true; }
                }
                if (paramsChanged) {
                    // odešleme nastavení na API
                    saveParameters(newParams);
                    sysParams = newParams;
                    // vyžádáme nové načtení dat
                    showProgressBar(true);
                    getConfigFromApi();
                    getTemperaturesFromApi();
                    // CToast.Info(this, "obnovuji data", 3);
                }
                else {
                    CToast.Info(this, "parametry se nezměnily", 2);
                }

            }
        }
        // návrat ze SettingsActivity
        else if (requestCode == 9) {
            if (data != null) {
                appSettings = (CSetting) data.getSerializableExtra("newSettings");
                saveConfiguration();
                if (appSettings != null && appSettings.url != null) {
                    showProgressBar(true);
                    getConfigFromApi();
                    getTemperaturesFromApi();
                }
            }
        }
    }

    protected void setNewRoomOrTemperature(String room, Double temperature) {
        // nastavíme novou místnost
        if (!heatingParams.selectedRoom.equals(room)) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "sensor");
                jsonData.put("value", room);
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // nastavíme novou teplotu
        if (!heatingParams.setTemperature.equals(temperature)) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "temperature");
                jsonData.put("value", temperature);
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CToast.Info(this, "parametry byly uloženy", 3);
    }

    protected void saveParameters(CSystemParams newParams) {
        if (newParams == null) return;
        // nastavime HEATING
        if (newParams.Heating != sysParams.Heating) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "heating");
                jsonData.put("value", newParams.Heating ? "true" : "false");
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // nastavíme HOT WATER
        if (newParams.HotWater != sysParams.HotWater) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "hotwater");
                jsonData.put("value", newParams.HotWater ? "true" : "false");
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // nastavíme ANTIFREEZE
        if (newParams.Antifreeze != sysParams.Antifreeze) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "antifreeze");
                jsonData.put("value", newParams.Antifreeze ? "true" : "false");
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // nastavíme DELTA
        if (newParams.Delta != sysParams.Delta) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("parameter", "delta");
                jsonData.put("value", newParams.Delta);
                sendConfigToApi(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CToast.Info(this, "parametry byly uloženy", 3);
    }

    protected void sendConfigToApi(JSONObject jsonData) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://" + appSettings.url + "/api/config";
        final String requestBody = jsonData.toString();

        StringRequest  jsonReq = new StringRequest (
                Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getConfigFromApi();
                getTemperaturesFromApi();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getConfigFromApi();
                getTemperaturesFromApi();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }
                return null;
            }
        };

        queue.add(jsonReq);
    }

    public void btnChangeRoomsHeatingClicked(View view) {
        Intent intent = new Intent(this, RoomHeatingActivity.class);
        intent.putExtra("HeatingParams", heatingParams);
        startActivityForResult(intent, 0);
    }

    public void btnChangeParamsClicked(View view) {
        Intent intent = new Intent(this, ParametersActivity.class);
        intent.putExtra("SystemParams", sysParams);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickMenuSetting(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("AppSettings", appSettings);
        startActivityForResult(intent, 9);
    }

    void showProgressBar(Boolean show) {
        ProgressBar pb = findViewById(R.id.progressBar);
        ImageView iv = findViewById(R.id.imageReload);
        if (show) {
            iv.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
        }
        else {
            pb.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.VISIBLE);
        }
    }

    public void refresh(View view) {
        showProgressBar(true);
        getConfigFromApi();
        getTemperaturesFromApi();
    }
}
