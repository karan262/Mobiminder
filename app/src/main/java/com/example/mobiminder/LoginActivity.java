package com.example.mobiminder;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    EditText Email_Login,Password_Login;
    TextView Forgot_Password;
    Button Signin;
    public static String URL_Login=IP_Manager.getIp()+"LoginUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        Email_Login=findViewById(R.id.Email_Login);
        Password_Login=findViewById(R.id.Password_Login);
        Forgot_Password=findViewById(R.id.Forgot_Password);
        Signin=findViewById(R.id.Signin);
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Email_Login.getText().toString().equals("")){
                    Email_Login.setError("Enter Username");
                }
                else if(Password_Login.getText().toString().equals("")){
                    Password_Login.setError("Enter Username");
                }
                else{
                    LoginUser();
                }

            }
        });

    }
    private void LoginUser(){
        final String memail=this.Email_Login.getText().toString().trim();
        final String mpassword=this.Password_Login.getText().toString().trim();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON",response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray=jsonObject.getJSONArray("login");
                            if (success.equals("1")) {
                                for (int i=0 ;i<jsonArray.length();i++)
                                {
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    String name=object.getString("Username").trim();
                                    String email=object.getString("Email").trim();
                                    String u_id=object.getString("u_id").trim();
                                    int u=Integer.parseInt(u_id);
                                    SharedPreferences sharedPreferences=getSharedPreferences("userData",MODE_PRIVATE);
                                    SharedPreferences.Editor shEditor=sharedPreferences.edit();
                                    shEditor.putInt("id",u);
                                    shEditor.commit();

                                    Toast.makeText(LoginActivity.this, "login Success", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);

                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "login error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "login fail" + e.toString(), Toast.LENGTH_SHORT).show();
                            // reg.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, "login failed" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",memail);
                params.put("password",mpassword);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}