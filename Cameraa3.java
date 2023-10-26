package com.example.uzhavu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Cameraa3 extends AppCompatActivity {

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    private int c=0;

    ImageButton btBrowser,btReset;
    ImageView imageView;
    Uri uri;
    Uri imageurl;
    Uri imageuri;
    EditText n;
    Button buclassify;
    TextView classitext;
    TextView link01;
    TextView link02;

    FirebaseStorage firebaseStorage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraa3);

        btBrowser = findViewById(R.id.bt_browse);
        btReset = findViewById(R.id.bt_reset);
        imageView = findViewById(R.id.image);

        buclassify=(Button)findViewById(R.id.classify);
        classitext=(TextView)findViewById(R.id.classifytext);
        link01=(TextView)findViewById(R.id.link1);
        link02=(TextView)findViewById(R.id.link2);

        firebaseStorage=FirebaseStorage.getInstance();
        storageRef=firebaseStorage.getReference();

        btBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                CropImage.startPickImageActivity(Cameraa3.this);

            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                imageView.setImageBitmap(null);
            }

        });

    }

    private void uploadToFirebase(byte[] bb,String rk) {

        String imgname= Integer.toString(c);

        StorageReference mountainsRef = storageRef.child("images/"+rk+"/"+imgname);

        mountainsRef.putBytes(bb)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content),"image uploaded.",Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"Uploaded!!!",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_LONG).show();
                    }
                });

        c = c+1;


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
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

        ///Model
        try{
            tflite=new Interpreter(loadmodelfile(this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        buclassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                ByteArrayOutputStream b=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,b);
                byte bb[]=b.toByteArray();
                showresult(bb);

            }
        });

    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showresult(byte[] bb){

        try{
            labels = FileUtil.loadLabels(this,"labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                classitext.setText(entry.getKey());

                String z = entry.getKey();
                if(z.contentEquals("BACTERIAL SPOT")){


                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://medium.com/@shyam12/bacterial-spot-3e7dffe52af0");
                        }
                    });



                    link02.setText("Bacterial spot is one of the most devastating diseases of pepper and tomato. The disease occurs worldwide where pepper and tomato are grown in warm, moist areas. When it occurs soon after transplanting and weather conditions remain favorable for disease development, the results are usually total crop loss.");
                }
                if(z.contentEquals("EARLY BLIGHT")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://www.youtube.com/watch?v=UKf5eCNBvBw");
                        }
                    });


                    link02.setText("Common on tomato and potato plants, early blight is caused by the fungus Alternaria solani and occurs throughout the United States. Symptoms first appear on the lower, older leaves as small brown spots with concentric rings that form a “bull’s eye” pattern. As the disease matures, it spreads outward on the leaf surface causing it to turn yellow, wither and die. Eventually the stem, fruit and upper portion of the plant will become infected. Crops can be severely damaged.");
                }
                if(z.contentEquals("NO DISEASE")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://www.thespruce.com/prevent-plant-diseases-in-your-garden-2539511");
                        }
                    });



                    link02.setText("NO DISEASE IS IDENTIFIED");
                }
                if(z.contentEquals("TARGET SPOT")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://plantpath.ifas.ufl.edu/u-scout/tomato/target-spot.html");
                        }
                    });



                    link02.setText("Target spot (or early blight) is one of the most common diseases attacking leaves and stems of potatoes. It usually spreads during autumn and is welcomed by some growers as a haulm killer. But it can cause losses if outbreaks occur early in the season, or in late maturing crops.");
                }
                if(z.contentEquals("MOSAIC VIRUS")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://www.planetnatural.com/pest-problem-solver/plant-disease/mosaic-virus/");
                        }
                    });


                    link02.setText(" Mosaic viruses affect more than 150 types of plants, including many fruits, vegetables, and flowers. The disease is characterized by leaves that are mottled with yellow, white, and light or dark green spots and streaks (in other words, a “mosaic” of these colors).");
                }

                uploadToFirebase(bb,z);
            }


        }
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }




    private void startCrop(Uri imageuri) {

        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);

    }
}