package com.example.uzhavu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class dash3 extends AppCompatActivity {

    CardView one;
    CardView two;
    CardView three;
    CardView four;
    CardView five;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash3);




        four = findViewById(R.id.ep1);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash3.this, stt.class));
            }
        });

        one = findViewById(R.id.rp1);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash3.this, tts.class));
            }
        });

        two = findViewById(R.id.rec);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash3.this, g5.class));
            }
        });

        two = findViewById(R.id.one1);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dash3.this, learn.class));
            }
        });




    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

}

