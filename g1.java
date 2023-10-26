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

public class g1 extends AppCompatActivity {

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
        setContentView(R.layout.activity_g1);

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

                CropImage.startPickImageActivity(g1.this);

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
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("yoga.tflite");
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
            labels = FileUtil.loadLabels(this,"yoga.txt");
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
                if(z.contentEquals("Vrischikasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/nYp07Pn24AY");
                        }
                    });

                    link02.setText("Vrischikasana or Scorpion pose is an inverted asana in modern yoga as exercise that combines a forearm balance and backbend; the variant with hands rather than forearms on the floor, elbows bent, is called Ganda Bherundasana.  ");
                }

                if(z.contentEquals("Utthita Parsvakonasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Utthita Parsvakonasana, Extended Side Angle Pose, is an asana in modern yoga as exercise. It is first described in 20th century texts.  ");
                }

                if(z.contentEquals("Halasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Halasana or Plough pose is an inverted asana in hatha yoga and modern yoga as exercise. Its variations include Karnapidasana with the knees by the ears, and Supta Konasana with the feet wide apart. ");
                }

                if(z.contentEquals("Natarajasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Natarajasana, Lord of the Dance Pose or Dancer Pose is a standing, balancing, back-bending asana in modern yoga as exercise. It is derived from a pose in the classical Indian dance form Bharatnatyam, which is depicted in temple statues in the Nataraja Temple, Chidambaram. ");
                }

                if(z.contentEquals("Chakrasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Chakrasana or Urdhva Dhanurasana is an asana in yoga as exercise. It is a backbend and is the first pose of the finishing sequence in Ashtanga Vinyasa Yoga.  ");
                }

                if(z.contentEquals("Sarvangasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Sarvangasana, Shoulder stand, or more fully Salamba Sarvangasana, is an inverted asana in modern yoga as exercise; similar poses were used in medieval hatha yoga.  ");
                }
                if(z.contentEquals("Trikonasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Trikonasana or Utthita Trikonasana, [Extended] Triangle Pose is a standing asana in modern yoga as exercise.  ");
                }
                if(z.contentEquals("Ustrasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Ustrasana, Ushtrasana, or Camel Pose is a kneeling back-bending asana in modern yoga as exercise. ");
                }
                if(z.contentEquals("Virabhadrasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Virabhadrasana (Sanskrit: वीरभद्रासन; IAST: Vīrabhadrāsana) or Warrior Pose is a group of related lunging standing asanas in modern yoga as exercise commemorating the exploits of a mythical warrior, Virabhadra. ");
                }
                if(z.contentEquals("Padmasana")){

                    link01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUrl("https://youtu.be/CU3HuPNsyG4");
                        }
                    });

                    link02.setText("Lotus position or Padmasana is a cross-legged sitting meditation pose from ancient India, in which each foot is placed on the opposite thigh. It is an ancient asana in yoga, predating hatha yoga, and is widely used for meditation in Hindu, Tantra, Jain, and Buddhist traditions. ");
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