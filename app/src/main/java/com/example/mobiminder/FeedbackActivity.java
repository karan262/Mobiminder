package com.example.mobiminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
Button btn;
RatingBar ratingBar;
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btn=findViewById(R.id.submit);
        ratingBar=findViewById(R.id.ratingBar);
//        Animation animation= AnimationUtils.loadAnimation(this,R.anim.animation_file);
//        textView.startAnimation(animation);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FeedbackActivity.this, ratingBar.getRating()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
}