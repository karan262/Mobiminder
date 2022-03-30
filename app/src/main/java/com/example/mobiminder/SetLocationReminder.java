package com.example.mobiminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetLocationReminder extends AppCompatActivity {
EditText loc,desc;
Button searchloc,insertReminder;
String plac;
Double lat,lon;
int u_id=0;
    private String URL_Location=IP_Manager.getIp()+"SearchAndAddReminder.php";
    private String URL_Reminder=IP_Manager.getIp()+"AddReminder.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_reminder);
        loc=findViewById(R.id.Place);
        desc=findViewById(R.id.ReminderDesc);
        searchloc=findViewById(R.id.SearchLocation);
        insertReminder=findViewById(R.id.InsertReminder);
        SharedPreferences sharedPreferences=getSharedPreferences("userData",MODE_PRIVATE);
        u_id=sharedPreferences.getInt("id",0);
        searchloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocationDB();
            }
        });
        insertReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertReminderDB();
            }
        });
        
    }

    private void searchLocationDB() {
        final String plc=this.loc.getText().toString().trim();
        final String description=this.desc.getText().toString().trim();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Location,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON",response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray=jsonObject.getJSONArray("loc");
                            if (success.equals("1")) {
                                for (int i=0 ;i<jsonArray.length();i++)
                                {
                                    JSONObject object=jsonArray.getJSONObject(i);
                                     plac=object.getString("Place").trim();
                                     lat=object.getDouble("Latitude");
                                     lon=object.getDouble("Longitude");


                                    Toast.makeText(SetLocationReminder.this, "Location Found at"+ lat + lon, Toast.LENGTH_SHORT).show();
                                }
                               insertReminder.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                Toast.makeText(SetLocationReminder.this, "Reminder error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SetLocationReminder.this, "Reminder fail" + e.toString(), Toast.LENGTH_SHORT).show();
                            // reg.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(SetLocationReminder.this, "Reminder failed" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("place",plc);
                params.put("desc",description);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void InsertReminderDB() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Reminder,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(SetLocationReminder.this, "Insert Success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(SetLocationReminder.this, "Insert error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SetLocationReminder.this, "Insert fail" + e.toString(), Toast.LENGTH_SHORT).show();
                            // reg.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(SetLocationReminder.this, "Insert failed" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("user_id",String.valueOf(u_id));
                params.put("place",plac);
                params.put("desc",desc.getText().toString());
                params.put("lat",String.valueOf(lat));
                params.put("long",String.valueOf(lon));
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}