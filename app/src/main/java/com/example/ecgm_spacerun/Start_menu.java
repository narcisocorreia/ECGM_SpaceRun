package com.example.ecgm_spacerun;


import android.content.AsyncQueryHandler;
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
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Start_menu extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    private int valor =0;

    public static final int RequestPermissionCode = 1;
    protected GoogleApiClient googleApiClient;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private double lat,lot;
    private Location BomJeus;
    private double bj=0;
    private Location Estg;
    private double estg=0;
    private Location PLocat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
        BomJeus = new Location("Point A");
        BomJeus.setLatitude( 41.554703);
        BomJeus.setLongitude(-8.377263);
        Estg = new Location("Point B");
        Estg.setLatitude(41.694624);
        Estg.setLongitude(-8.846903);




        PLocat= new Location("Point P");



        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Obter as coordnadas no momento
                            if (location != null) {
                                lat=location.getLatitude();
                                lot=location.getLongitude();
                            }
                        }
                    });
        }

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(Start_menu.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Start_menu", "Connection suspendedd");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Start_Menu", "Connection failed: " + connectionResult.getErrorCode());
    }


    public void StartGame(View view){

        //Criar a localização do jogador
        PLocat.setLatitude(lat);
        PLocat.setLongitude(lot);

        //Calcular e verifcar as distancias
        bj = BomJeus.distanceTo(PLocat);
        estg= Estg.distanceTo(PLocat);
        if(bj<=100){
            valor=1;
        }
        if(estg<=100){
            valor=2;
        }

        Log.e("BOMM", String.valueOf(bj+"-------"+estg));

        //Inicar os jogo
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("valor", valor);
        startActivity(intent);

    }


    public void HowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlay.class);
        startActivity(intent);
    }
}
