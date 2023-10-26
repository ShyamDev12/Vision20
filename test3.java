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

public class test3 extends AppCompatActivity {

    private TextView moisture1;
    private Button btn;
    private DatabaseReference mDatabase,mDatabase1,mDatabase2;

    String status;
    String TAG;

    int one1,two1,three1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        btn = findViewById(R.id.btn1);
        moisture1 = findViewById(R.id.one2);




        mDatabase = FirebaseDatabase.getInstance().getReference().child("Benzene");



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


            }
        });
    }
}