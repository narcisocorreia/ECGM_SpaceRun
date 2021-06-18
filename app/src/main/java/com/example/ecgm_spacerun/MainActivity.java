package com.example.ecgm_spacerun;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Class do jogo
    private GameView gameView;
    private Handler handler = new Handler();
    private final static long TIMER_INTERVAL = 30;

    //Sensor
    private SensorManager mSensorManager;
    private Sensor mSensor;

    //Location
    public static final int RequestPermissionCode = 1;
    protected GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location PlayerLocat;
    private Location BomJesus;
    private Location ESTG;

    private  boolean helper=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BomJesus = new Location("Point A");
        BomJesus.setLatitude( 41.701332);
        BomJesus.setLongitude(-8.834940);
        ESTG = new Location("Point B");
        ESTG.setLatitude(41.694624);
        ESTG.setLongitude(-8.846903);
        PlayerLocat= new Location("Point P");




        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();

         int aux = intent.getIntExtra("valor",0);

        gameView= new GameView(this, aux );
        setContentView(gameView);

        Timer timer =  new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Location();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gameView.invalidate();
                    }
                });
            }
        },0,TIMER_INTERVAL);

        mSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(mSensor==null){
            finish();;
        }

        //Som de fundo
        MediaPlayer mp = MediaPlayer.create(this,R.raw.bgsound);
        mp.setVolume(0.5f,0.5f);
        mp.setLooping(true);
        mp.start();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Função para movimentar a nave
        if (event.values[0]<mSensor.getMaximumRange()){
            gameView.touch_flg=true;
        }
        else {
            gameView.touch_flg=false;
        }

    }

    public void onResume(){
        super.onResume();

        mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause(){
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onConnected(@Nullable Bundle bundle) {
        Location();
    }

    private void Location(){
        Log.e("BOMM","Part 1");
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if(location!=null){
                                Log.e("BOMM","Part 2");
                                DistanceCheck(location);
                            }else
                                if (helper==true && location ==null){
                                gameView.SelectImage(0);
                                helper =false;
                            }
                        }
                    });
        }
    }

    private void DistanceCheck(Location l) {
        Log.e("BOMM","Part 3");
        if(PlayerLocat.getLatitude()!=l.getLatitude() || PlayerLocat.getLongitude()!=l.getLongitude()){
            Log.e("BOMM","Part 4");

            PlayerLocat.setLongitude(l.getLongitude());
            PlayerLocat.setLatitude(l.getLatitude());

            float bj = BomJesus.distanceTo(PlayerLocat);
            float estg= ESTG.distanceTo(PlayerLocat);
            if(bj<=100){
                gameView.SelectImage(1);
                Log.e("BOMM","bj ->"+ String.valueOf(bj));
            }
            if(estg<=100){
                gameView.SelectImage(2);
                helper=!helper;
                Log.e("BOMM","estg ->"+ String.valueOf(estg));
            }
            if(estg>100&& bj>100){
                gameView.SelectImage(0);
            }

            if(helper==false){
                helper=true;
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }


    public void onConnectionSuspended(int i) {
        Log.e("Start_menu", "Connection suspendedd");
    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Start_Menu", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
