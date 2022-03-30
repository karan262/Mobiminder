package com.example.mobiminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    ConstraintLayout addtodo,mytodo,SetReminderContainer,RatingContainer,SchedulesContainer;
    MyBackgroundService mService=null;
    boolean mBound=false;
    private final ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder=(MyBackgroundService.LocalBinder)iBinder;
            mService=binder.getService();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService=null;
            mBound=false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Dexter.withActivity(this).withPermissions(Arrays.asList(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        ));
        bindService(new Intent(MainActivity.this,MyBackgroundService.class),mServiceConnection,Context.BIND_AUTO_CREATE);
        addtodo=findViewById(R.id.AddToDoContainer);
        mytodo=findViewById(R.id.MYToDoContainer);
        SetReminderContainer=findViewById(R.id.SetReminderContainer);
        RatingContainer=findViewById(R.id.RatingContainer);
        SchedulesContainer=findViewById(R.id.SchedulesContainer);
        addtodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.requestLocationUpdates();

                Intent intent=new Intent(getApplicationContext(),AddToDoList.class);
                startActivity(intent);
            }
        });
        mytodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MyToDoActivity.class);
                startActivity(intent);
            }
        });
        SetReminderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SetLocationReminder.class);
                startActivity(intent);
            }
        });
        RatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FeedbackActivity.class);
                startActivity(intent);
            }
        });
        SchedulesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ReminderActivity.class);
                startActivity(intent);
            }
        });
//        startService(new Intent(getApplicationContext(),AppService.class));

//        sendLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mServiceConnection);
            mBound=false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void sendLocationUpdates(){
        Intent intent=new Intent(this,LocationAlarmBroadCast.class);
        intent.setAction("AlarmLocation");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0,10,pendingIntent);
        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES)){

        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event){
        if(event!=null){
            String data=new  StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            Toast.makeText(mService, data, Toast.LENGTH_SHORT).show();
        }
    }
}