package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    JSONArray temperArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                            String place = configJson.getString("sensor");


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

        for (int i = 0; i < temperArray.length(); i++) {
            JSONObject jo = temperArray.getJSONObject(i);
            String place = jo.getString("place");
            Double temperature = jo.getDouble("temperature");

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
                tvTemperature.setTextColor(Color.parseColor("#00ff00"));
            }

            row.addView(tvRoom);
            row.addView(tvTemperature);
            table.addView(row, i);
        }
    }


    /*protected void getTemperaturesFromApi2() throws IOException {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://pistovice.spanhel.eu:1080/api/temperatures");
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Android-0.1");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    if (urlConnection.getResponseCode() == 200) {
                        InputStream responseBody = urlConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key
                            if (key.equals("organization_url")) { // Check if desired key
                                // Fetch the value as a String
                                String value = jsonReader.nextString();

                                // Do something with the value
                                // ...

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }
                        }

                        jsonReader.close();

                    } else {
                        // Error handling code goes here
                    }
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/

    protected void btnChangeRoomsHeatingClicked(View view) {
        Intent intent = new Intent(this, RoomHeatingActivity.class);

        CRoomHeatingParams heatingParams = new CRoomHeatingParams();
        heatingParams.rooms = new String[]{"Loznice", "Kuchyn", "Obyvak", "Kotelna", "Pokojik"};
        heatingParams.selectedRoom = heatingParams.rooms[0];
        heatingParams.setTemperature = 20.0;

        intent.putExtra("HeatingParams", heatingParams);
        startActivity(intent);
    }

    protected void btnChangeParamsClicked(View view) {
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
