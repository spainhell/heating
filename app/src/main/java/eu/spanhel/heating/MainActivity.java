package eu.spanhel.heating;

import android.content.Intent;
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
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private JSONArray temperArray;
    private CSystemParams sysParams;
    private CRoomHeatingParams heatingParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getConfigFromApi();
        getTemperaturesFromApi();
    }

    protected void getConfigFromApi() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pistovice.spanhel.eu:1080/api/config";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
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
        String url ="https://pistovice.spanhel.eu:1080/api/temperatures";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // z API prijde pole teplot
                            temperArray = new JSONArray(response);

                            TextView tvDateTime = findViewById(R.id.label_datetime);
                            tvDateTime.setText(String.format("%s", temperArray.getJSONObject(0).get("time")));

                            addRoomsToTable();

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
            // TextView teploty = findViewById(R.id.label_temperatures);
            // teploty.setText("OK");
            if (data != null) {
                String room = data.getStringExtra("room");
                Double temperature = data.getDoubleExtra("temperature", 0.0);
                setNewRoomOrTemperature(room, temperature);
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
    }

    protected void sendConfigToApi(JSONObject jsonData) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pistovice.spanhel.eu:1080/api/config";
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

        CSystemParams systemParams = new CSystemParams();
        systemParams.Antifreeze = false;
        systemParams.Heating = false;
        systemParams.HotWater = true;
        systemParams.Delta = 0.5;

        intent.putExtra("SystemParams", systemParams);
        startActivity(intent);
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

}
