package com.example.cmala.securitycheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecurityCheck";
    private static RequestQueue requestQueue;
    TextView output;
    char[] passParams = {'C', 'v', 'V', 'N', '#', 'c', 'R'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        final Button button = findViewById(R.id.button);
        final Button button2 = findViewById(R.id.button2);
        final EditText input = findViewById(R.id.input);
        output = findViewById(R.id.return_statement);
        output.setMovementMethod(new ScrollingMovementMethod());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input.getText().toString().contains("@")) {
                    output.setText("Invalid account.");
                } else {
                    getBreaches(input.getText().toString());
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordParams = createPasswordParams();
                createPassword(passwordParams);
            }
        });

    }

    void getBreaches(String input) {
        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    "https://haveibeenpwned.com/api/v2/breachedaccount/" + input + "?truncateResponse=true",
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(final JSONArray response) {
                            Log.d(TAG, response.toString());
                            goThroughArray(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                    output.setText("Invalid account or no account breaches have been detected.");
                }
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void goThroughArray(final JSONArray response) {
        try {
            String toShow = "";
            for (int i = 0; i < response.length(); i++) {
                toShow += response.getJSONObject(i).getString("Name") + " ";
            }
            output.setText("Change your password! " + response.length()+ " breaches have been found. Accounts compromised at: " + toShow);
        } catch (JSONException ignored) {
            System.out.print("shit");
        }
    }
    public String createPasswordParams() {
        Random random = new Random();
        int rand;
        String toR = "";
        for (int i = 0; i < 16; i++) {
            rand = random.nextInt(passParams.length);
            toR = String.valueOf(passParams[rand]);
        }
        return toR;
    }
    void createPassword(String params) {
        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    "https://www.passwordrandom.com/query?command=password&format=json&count=1&scheme=" + params,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(final JSONArray response) {
                            Log.d(TAG, response.toString());
                            displayPassword(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void displayPassword(final JSONArray response) {
        try {
            output.setText(response.getJSONObject(0).getString("char"));
        } catch (JSONException ignored) {

        }
    }
}
