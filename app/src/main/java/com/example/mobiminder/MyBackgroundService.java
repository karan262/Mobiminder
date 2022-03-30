package com.example.mobiminder;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

public class MyBackgroundService extends Service {
    private static final String CHANEL_ID="my_chanel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.example.mobiminder"+".started_from_notification";
    private final IBinder mBinder= new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL=10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL=UPDATE_INTERVAL_IN_MIL/2;
    private static final int NOTI_ID=1223;
    private boolean mChangingConfiguration=false;
    private NotificationManager mNotificationManager;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    public void requestLocationUpdates() {
        Common.setRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(),MyBackgroundService.class));
        try{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        }
        catch (SecurityException e){
            Log.e("Aniket","Lost Location permission Could not request it"+e);
        }
    }

    public class LocalBinder extends Binder {
        MyBackgroundService getService(){return MyBackgroundService.this;}

    }
    public MyBackgroundService(){

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration=true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration=false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration=false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration && Common.requestingLocationUpdates(this))
//            Toast.makeText(this, "aws server", Toast.LENGTH_SHORT).show();
//            if(mLocation.getLatitude()==22.9968 && mLocation.getLongitude()==72.5170)
                startForeground(NOTI_ID,getNotification());
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread=new HandlerThread("aniket");
        handlerThread.start();
        mServiceHandler=new Handler(handlerThread.getLooper());
        mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel mChanel=new NotificationChannel(CHANEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChanel);
        }
    }

    private void getLastLocation() {
        try{
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult()!=null)
                                mLocation=task.getResult();
                            else
                                Log.e("aniket","Failed to get location");
                        }
                    });
        }
        catch (SecurityException ex){
            Log.e("aniket","Lost Location Permission "+ex);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification=intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false);
        if(startedFromNotification){
            removeLocationUpdate();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void removeLocationUpdate() {
        try{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Common.setRequestingLocationUpdates(this,false);
            stopSelf();
        }
        catch (SecurityException ex){
           Common.setRequestingLocationUpdates(this,true);
           Log.e("aniket","Lost Location Permission could not remove updates "+ex);

        }

    }

    private void createLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MIL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void onNewLocation(Location lastLocation) {
        mLocation=lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));
        if(serviceIsRunningInForeGround(this)){
            mNotificationManager.notify(NOTI_ID, getNotification());

        }
    }

    private Notification getNotification() {
        Intent intent=new Intent(this,MyBackgroundService.class);
        String text= Common.getLocationText(mLocation);

            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
            PendingIntent servicePending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent activityPending = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .addAction(R.drawable.add_to_do, "Launch", activityPending)
                    .addAction(R.drawable.my_todo, "Remove", servicePending)
                    .setContentText(text)
                    .setContentTitle(Common.getLocationTitle(this))
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANEL_ID);
            }
            return builder.build();

    }

    private boolean serviceIsRunningInForeGround(MyBackgroundService myBackgroundService) {
        ActivityManager manager=(ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo:manager.getRunningServices(Integer.MAX_VALUE)){
            if(getClass().getName().equals(serviceInfo.service.getClassName())){
                if(serviceInfo.foreground){
                    return true;
                }
            }
        }

        return false;
    }
}
