package com.example.uzhavu;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Cameraa2 extends AppCompatActivity {

    ImageButton btBrowser,btReset;
    ImageView imageView;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraa2);

        btBrowser = findViewById(R.id.bt_browse);
        btReset = findViewById(R.id.bt_reset);
        imageView = findViewById(R.id.image_view);

        btBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                CropImage.startPickImageActivity(Cameraa2.this);

            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View v){
                  imageView.setImageBitmap(null);
              }

        });

    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 0);
            } else {

                startCrop(imageuri);

            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageView.setImageURI(result.getUri());
                Toast.makeText(this,"Image Update Successfully !!!"
                ,Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startCrop(Uri imageuri) {

        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);

    }
}