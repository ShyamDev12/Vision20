package com.example.uzhavu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;//gig video from legend and i think 180 and 3000 is cool

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Splash.this, "!W-E-L-C-O-M-E!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Splash.this,dash.class));
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}