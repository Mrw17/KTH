package com.example.ii142x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.ii142x.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    Button btnHeartRate;
    Button btnGPS;
    Button btnPressure;
    Button btnAccelerometer;
    Button btnPhoto;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpGUI();
    }

    private void setUpGUI(){
        btnHeartRate = findViewById(R.id.btnGetHeartRate);
        btnHeartRate.setOnClickListener(v-> sendUserToHeartRateActivity());

        btnGPS = findViewById(R.id.btnGetGPS);
        btnGPS.setOnClickListener(v-> sendUserToGPSActivity());

        btnPressure = findViewById(R.id.btnGetPressure);
        btnPressure.setOnClickListener(v-> sendUserToPressureActivity());

        btnAccelerometer = findViewById(R.id.btnAccelerometer);
        btnAccelerometer.setOnClickListener(v-> sendUserToAccelerometerActivity());

        btnPhoto = findViewById(R.id.btnGetPhoto);
        btnPhoto.setOnClickListener(v-> sendUserToPhotoActivity());

    }

    private void sendUserToHeartRateActivity() {
        Intent intent = new Intent(this, HeartRateActivity.class);
        this.startActivity(intent);
    }

    private void sendUserToGPSActivity() {
        //Intent intent = new Intent(this, GPSActivity.class);
        //this.startActivity(intent);
    }

    private void sendUserToPressureActivity() {
       // Intent intent = new Intent(this, PressureActivity.class);
      //  this.startActivity(intent);
    }

    private void sendUserToAccelerometerActivity() {
       // Intent intent = new Intent(this, AccelerometerActivity.class);
      //  this.startActivity(intent);
    }

    private void sendUserToPhotoActivity() {
      //  Intent intent = new Intent(this, PhotoActivity.class);
     //   this.startActivity(intent);
    }

}