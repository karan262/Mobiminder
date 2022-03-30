package com.example.mobiminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyToDoActivity extends AppCompatActivity {
    RequestQueue queue;
    JsonArray array;
    String t_title[], t_desc[], t_date[],t_id[];
    RecyclerView recyclerView;
    int u_id;
    private String URL_TODO = IP_Manager.getIp() + "MyToDo.php";
    private String URL_DELETE_TODO = IP_Manager.getIp() + "DeleteToDo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_to_do);
        recyclerView = findViewById(R.id.MyToDoRecycle);
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
                        if (res.contains("u_id") && res.contains("T_title") && res.contains("T_description")) {

                            array = gson.fromJson(res, JsonArray.class);
                            t_title = new String[array.size()];
                            t_desc = new String[array.size()];
                            t_date = new String[array.size()];
                            t_id = new String[array.size()];

                            for (int i = 0; i < array.size(); i++) {
                                System.out.println("in loop");
                                JsonObject jobj = array.get(i).getAsJsonObject();
                                t_title[i] = jobj.get("T_title").getAsString();
                                t_desc[i] = jobj.get("T_description").getAsString();
                                t_date[i] = jobj.get("T_date").getAsString();
                                t_id[i] = jobj.get("T_id").getAsString();


                            }
                            ToDoListAdapter toDoListAdapter = new ToDoListAdapter(context, t_title, t_desc, t_date);
                            recyclerView.setAdapter(toDoListAdapter);
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

    public void EditToDoList(int i) {
        Intent intent=new Intent(getApplicationContext(),EditToDo.class);
        intent.putExtra("u_id",u_id);
        intent.putExtra("t_title",t_title[i]);
        intent.putExtra("t_desc",t_desc[i]);
        intent.putExtra("t_date",t_date[i]);
        intent.putExtra("t_id",t_id[i]);
        Toast.makeText(this, "t_id ="+t_id[i], Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void DeleteToDoList(final int i) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_DELETE_TODO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON",response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(MyToDoActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MyToDoActivity.this, "Something Went Wrong" + e.toString(), Toast.LENGTH_SHORT).show();
                            // reg.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MyToDoActivity.this, "Something Went Wrong" + error.toString(), Toast.LENGTH_SHORT).show();
                // reg.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("U_id",String.valueOf(u_id));
                params.put("T_id",String.valueOf(t_id[i]));

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}