package com.example.uzhavu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class three extends AppCompatActivity {

    private TextView moisture1,one,two,three,four;
    private Button btn;
    private DatabaseReference mDatabase,mDatabase1,mDatabase2,mDatabase3,mDatabase4;

    String status;

    String TAG;

    int one1,two1,three1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        btn = findViewById(R.id.btn1);
        moisture1 = findViewById(R.id.one3);
        one = findViewById(R.id.o1);
        two = findViewById(R.id.o2);
        three = findViewById(R.id.o3);
        four = findViewById(R.id.o4);



        mDatabase = FirebaseDatabase.getInstance().getReference().child("TDS");
        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Temperature");
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Moisture");
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Humidity");
        mDatabase4 = FirebaseDatabase.getInstance().getReference().child("Distance");



        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            moisture1.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mDatabase1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            one.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mDatabase2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            three.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mDatabase3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            two.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mDatabase4.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data = snapshot.getValue().toString();
                            four.setText(data);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

    }
}