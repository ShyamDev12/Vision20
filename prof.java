package com.example.uzhavu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class prof extends AppCompatActivity {

    private int progress = 0;

    private ProgressBar progressBar;
    private TextView textViewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof);
        progressBar = findViewById(R.id.progress_bar);
        textViewProgress = findViewById(R.id.text_view_progress);

        progressBar.setProgress(0);
        textViewProgress.setText("0%");




    }
    public void onClickUp(View view)
    {
        if (progress <= 90)
        {
            progress += 10;
            updateProgressBar();
        }
    }

    public void onClickLow(View view)
    {
        if (progress >= 10)
        {
            progress -= 10;
            updateProgressBar();
        }
    }

    private void updateProgressBar()
    {
        progressBar.setProgress(progress);
        textViewProgress.setText(progress + "%");
        if (progress == 100){
            startActivity(new Intent(prof.this,sscard.class));
        }
    }
}