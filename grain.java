package com.example.uzhavu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class grain extends AppCompatActivity {

    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    ImageView iv4;
    ImageView iv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grain);


        iv1 = findViewById(R.id.q1);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(grain.this,g1.class));
            }
        });

        iv2 = findViewById(R.id.q2);
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(grain.this,g2.class));
            }
        });

        iv3 = findViewById(R.id.q3);
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(grain.this,g3.class));
            }
        });

        iv4 = findViewById(R.id.q4);
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(grain.this,g4.class));
            }
        });

        iv5 = findViewById(R.id.q5);
        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(grain.this,g5.class));
            }
        });
    }
}