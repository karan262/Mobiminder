package com.example.mobiminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {
    RequestQueue queue;
    JsonArray array;
    String t_place[], t_desc[], t_latitude[],t_longitude[];
    RecyclerView recyclerView;
    int u_id;
    private String URL_TODO = IP_Manager.getIp() + "ReminderUser.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        recyclerView = findViewById(R.id.ReminderRecycle);
        queue = Volley.newRequestQueue(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        u_id = sharedPreferences.getInt("id", 0);
        final Context context = this;
        StringRequest makeRequest = new StringRequest(Request.Method.POST, URL_TODO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        System.out.println(res);
                        Gson gson = new Gson();
                        if (res.contains("u_id") && res.contains("place") && res.contains("description")) {

                            array = gson.fromJson(res, JsonArray.class);
                            t_place = new String[array.size()];
                            t_desc = new String[array.size()];
                            t_latitude = new String[array.size()];
                            t_longitude = new String[array.size()];

                            for (int i = 0; i < array.size(); i++) {
                                System.out.println("in loop");
                                JsonObject jobj = array.get(i).getAsJsonObject();
                                t_place[i] = jobj.get("place").getAsString();
                                t_desc[i] = jobj.get("description").getAsString();
                                t_latitude[i] = jobj.get("latitude").getAsString();
                                t_longitude[i] = jobj.get("longitude").getAsString();


                            }
                            ReminderAdapter reminderAdapter = new ReminderAdapter(context, t_place, t_desc, t_latitude,t_longitude);
                            recyclerView.setAdapter(reminderAdapter);
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Once the request is performed, failed code over here is executed
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", String.valueOf(u_id));
                return params;
            }
        };
        queue.add(makeRequest);

    }
}