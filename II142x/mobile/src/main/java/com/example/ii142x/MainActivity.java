package com.example.ii142x;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * MainActivity that will show a main menu for user.
 * User can choose between.
 * read heart rate and send it to mobile.
 * read GPS and send it to mobile.
 * read pressure and send it to mobile.
 * read accelerometer and send it to mobile.
 * send/receive a photo form mobile.
 */
public class MainActivity extends AppCompatActivity {

    Button btnAccelerometer;
    Button btnHeartBeat;
    Button btnGPS;
    Button btnPressure;
    Button btnPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpGUI();
    }

    /**
     * Default GUI and listeners on buttons
     */
    private void setUpGUI() {
        btnAccelerometer = findViewById(R.id.btnMainAccelerometer);
        btnAccelerometer.setOnClickListener(v-> accelerometerBtnPressed());

        btnHeartBeat = findViewById(R.id.btnMainHeartRate);
        btnHeartBeat.setOnClickListener(v-> heartBeatBtnPressed());

        btnGPS = findViewById(R.id.btnMainGPS);
        btnGPS.setOnClickListener(v-> gpsBtnPressed());

        btnPressure = findViewById(R.id.btnMainPressure);
        btnPressure.setOnClickListener(v-> pressureBtnPressed());

        btnPhoto = findViewById(R.id.btnMainPhoto);
        btnPhoto.setOnClickListener(v-> photoBtnPressed());
    }

    private void heartBeatBtnPressed() {
        Intent intent = new Intent(this, HeartRateActivity.class);
        this.startActivity(intent);
    }

    private void gpsBtnPressed() {
        Intent intent = new Intent(this, GPSActivity.class);
        this.startActivity(intent);
    }

    private void pressureBtnPressed() {
        Intent intent = new Intent(this, PressureActivity.class);
        this.startActivity(intent);
    }

    private void accelerometerBtnPressed() {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        this.startActivity(intent);
    }

    private void photoBtnPressed() {
        Intent intent = new Intent(this, PhotoActivity.class);
        this.startActivity(intent);
    }
}