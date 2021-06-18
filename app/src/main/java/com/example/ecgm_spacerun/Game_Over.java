package com.example.ecgm_spacerun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Game_Over extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game__over);
    }

    public void TryAgain(View view){
        startActivity(new Intent(getApplicationContext(),Start_menu.class));
    }
}
