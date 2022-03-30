package com.example.mobiminder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText Username,mobile_number,Email,Password,Confirm_pwd;
    Button btnRegister;
    public static String URL_REGIST=IP_Manager.getIp()+"RegisterUser.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        Username=findViewById(R.id.Username);
        mobile_number=findViewById(R.id.mobile_number);
        Email=findViewById(R.id.Email);
        Password=findViewById(R.id.Password);
        Confirm_pwd=findViewById(R.id.ConfirmPwd);
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Username.getText().toString().equals("")){
                    Username.setError("Enter Username");
                }
                else if(mobile_number.getText().toString().equals("")){
                    mobile_number.setError("Enter mobile number");
                }
                else if(Email.getText().toString().equals("")){
                    Email.setError("Enter Email");
                }
                else if(Password.getText().toString().equals("")){
                    Password.setError("Enter Password");
                }
                else if(Confirm_pwd.getText().toString().equals("")){
                    Confirm_pwd.setError("Enter Confirm Password");
                }
                else{
                    RegisterUser();
                }
            }

        });
    }
    private void RegisterUser(){
        final String User_name,mobile,email,pwd,confirm_pwd;
        User_name=Username.getText().toString().trim();
        mobile=mobile_number.getText().toString().trim();
        email=Email.getText().toString().trim();
        pwd=Password.getText().toString().trim();
        confirm_pwd=Confirm_pwd.getText().toString().trim();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON",response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Register fail" + e.toString(), Toast.LENGTH_SHORT).show();
                            // reg.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Register failed" + error.toString(), Toast.LENGTH_SHORT).show();
                // reg.setVisibility(View.GONE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Username",User_name);
                params.put("mobile_number",mobile);
                params.put("email",email);
                params.put("password",pwd);
                params.put("confirm_pwd",confirm_pwd);

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}