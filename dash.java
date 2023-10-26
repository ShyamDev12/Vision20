package com.example.uzhavu;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

public class dash extends AppCompatActivity {

    CardView one;
    CardView two;
    CardView three;
    CardView four;
    CardView five;
    CardView six;
    CardView seven;
    CardView eight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        one=findViewById(R.id.rew);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash.this,prof.class));
            }
        });



        three=findViewById(R.id.sa1);

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash.this,dash2.class));
            }
        });

        four=findViewById(R.id.ep1);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash.this,handy.class));
            }
        });






    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}