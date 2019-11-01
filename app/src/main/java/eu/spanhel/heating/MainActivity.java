package eu.spanhel.heating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTemperaturesFromApi();
    }

    protected void getTemperaturesFromApi() {
        //final String[] temperatures = new String[1];

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pistovice.spanhel.eu:1080/api/temperatures";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            TableLayout table = findViewById(R.id.tableLayout);

                            JSONArray obj = new JSONArray(response);

                            TextView labelTemperatures = findViewById(R.id.label_temperatures);
                            labelTemperatures.setText(String.format("Teploty v %s", obj.getJSONObject(0).get("time")));

                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject jo = obj.getJSONObject(i);
                                String a = jo.getString("time");
                            }


                            EditText et = findViewById(R.id.editText);
                            //et.setText(obj.getString());
                        } catch (JSONException e) {
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


    protected void getTemperaturesFromApi2() throws IOException {
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

    }

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
