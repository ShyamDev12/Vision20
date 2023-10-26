package com.example.uzhavu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class pay extends AppCompatActivity implements PaymentResultListener {

    Button btpay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        btpay = findViewById(R.id.bt_pay);

        String sAmount = "100";

        int amount = Math.round(Float.parseFloat(sAmount)*100);

        btpay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){

                Checkout checkout = new Checkout();

                checkout.setKeyID("rzp_test_GqJDdGCcLC0dEQ");

                checkout.setImage(R.drawable.rzp_logo);

                JSONObject object = new JSONObject();
                try{
                    object.put("name","Aramm");
                    object.put("description","Test payment");
                    object.put("theme.color","#0093");
                    object.put("currency","INR");
                    object.put("amount",amount);
                    object.put("prefill.contact","9150155684");
                    object.put("prefill.email","shyam50400405@gmail.com");
                    checkout.open(pay.this,object);

                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();

    }
}