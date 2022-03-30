package com.example.mobiminder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splashscreen extends AppCompatActivity {
    private ImageView iv;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getSupportActionBar().hide();
        iv=(ImageView)findViewById(R.id.SplashImage);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(animation);
        if(getSharedPreferences("userData",MODE_PRIVATE).contains("id")) {
              intent = new Intent(this, MainActivity.class);
        }
        else{
//              intent = new Intent(this, RegisterActivity.class);
//            intent = new Intent(this, LoginActivity.class);
            intent = new Intent(this, MainActivity.class);


        }
        Thread timer=new Thread(){
            @Override
            public void run() {
                try
                {
                    sleep(6000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}