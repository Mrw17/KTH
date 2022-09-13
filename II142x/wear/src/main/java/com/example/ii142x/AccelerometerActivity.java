package com.example.ii142x;

import android.Manifest;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.ii142x.DTO.AccelerometerDTO;
import com.example.ii142x.communication.AssetKeys;
import com.example.ii142x.communication.MessagePath;
import com.example.ii142x.databinding.ActivityAccelerometerBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.*;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Class that handles accelerometer-sensor.
 * It will show the value to smartwatch and send it to mobile over DataClient.
 */
public class AccelerometerActivity extends Activity implements SensorEventListener, EasyPermissions.PermissionCallbacks {
    private final int REQUEST_BODY_SENSOR = 2;
    SensorManager sensorManager;
    private TextView textViewAccelerometer_1;
    private TextView textViewAccelerometer_2;
    private TextView textViewAccelerometer_3;
    private Button btnAccelerometer;

    private static final String[] ACCELEROMETER =
            {Manifest.permission.BODY_SENSORS};

    /**
     * When application start it will get SensorManager and
     * set up the GUI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccelerometerBinding binding = ActivityAccelerometerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sensorManager = (SensorManager) getBaseContext().getSystemService(SENSOR_SERVICE);
        setUpGUI();
    }


    /**
     * When user pause the application it will
     * remove listener on dataClient
     */
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Default GUI
     */
    private void setUpGUI(){
        btnAccelerometer = findViewById(R.id.btnAccelerometerStart);
        btnAccelerometer.setOnClickListener(v-> btnAccelerometerPressed());

        Button btnBack = findViewById(R.id.btnAccelerometerBack);
        btnBack.setOnClickListener(v-> sendUserToMainActivity());

        textViewAccelerometer_1 = findViewById(R.id.textViewAccelerometer1);
        textViewAccelerometer_2 = findViewById(R.id.textViewAccelerometer2);
        textViewAccelerometer_3 = findViewById(R.id.textViewAccelerometer3);
    }


    /**
     * Closes activity
     */
    private void sendUserToMainActivity() {
        finish();
    }

    /**
     * Init process of sending data to phone
     * @param accelerometerDTO data to be sent
     */
    private void sendToPhone(AccelerometerDTO accelerometerDTO) {
        new Thread(() -> {
            Asset asset = toAsset(accelerometerDTO);
            startSending(asset);
        }).start();
    }

    /**
     * Sending an asset with DataClient
     * @param asset to be sent
     */
    private void startSending(Asset asset) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(MessagePath.ACCELEROMETER);

        dataMap.getDataMap().putAsset(AssetKeys.ACCELERATOR, asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());

        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask.addOnSuccessListener(
                dataItem -> System.out.println("Sending image was successful: " + dataItem));
    }


    /**
     * Converts a AccelerometerDTO to asset
     * @param accelerometerDTO to be converted
     * @return the converted asset
     */
    private static Asset toAsset(AccelerometerDTO accelerometerDTO) {
        try {
            return Asset.createFromBytes(accelerometerDTO.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When Accelerometer button is pressed it will
     * request permissions if it is missing
     * then start reading the sensor
     */
    private void btnAccelerometerPressed() {
        requestBodySensorPermission();

        if(btnAccelerometer.getText().equals(getString(R.string.btnStartReadAccelerometer))){
            startReadingAccelerator();
            btnAccelerometer.setText(R.string.btnStopReadAccelerometer);
        }
        else{
            stopReadingAccelerator();
            btnAccelerometer.setText(R.string.btnStartReadAccelerometer);
        }
    }

    /**
     * Stop reading accelerator
     */
    private void stopReadingAccelerator() {
        sensorManager.unregisterListener(this);
    }

    /**
     * Start reading accelerator
     */
    private void startReadingAccelerator() {
        if (hasAccelerometerPermissions()) {
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * If permission is granted it will start reading accelerator
     * @param requestCode to track sensor
     * @param perms permissions
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startReadingAccelerator();
    }

    /**
     * If permission is denied
     * @param requestCode for tracking
     * @param perms that was denied
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToastToUser("Need permission to body sensor to be able to read accelerometer");
    }

    /**
     * Callback for the result of requesting permissions
     * @param requestCode request code for tracking
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     *
     * @return status of accelerometer permissions
     */
    private boolean hasAccelerometerPermissions() {
        return (EasyPermissions.hasPermissions(this, ACCELEROMETER));
    }


    /**
     * When a new sensor event is happening
     * it will control that it is accelerometer-sensor
     * and update GUI with new values and
     * send it to mobile
     * @param sensorEvent that happened
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            AccelerometerDTO accelerometerDTO = new AccelerometerDTO(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            updateGUIOnNewAccelerometerValues(accelerometerDTO);
            sendToPhone(accelerometerDTO);
        }
    }

    /**
     * Updated GUI with new data
     * @param accelerometerDTO to be shown to user
     */
    private void updateGUIOnNewAccelerometerValues(AccelerometerDTO accelerometerDTO) {
        textViewAccelerometer_1.setText(String.valueOf(accelerometerDTO.getX()));
        textViewAccelerometer_2.setText(String.valueOf(accelerometerDTO.getY()));
        textViewAccelerometer_3.setText(String.valueOf(accelerometerDTO.getZ()));
    }

    /**
     * When accuracy changes
     * @param sensor that has been changed
     * @param i for tracking sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /**
     * Request body sensor permission if it is missing
     */
    @AfterPermissionGranted(REQUEST_BODY_SENSOR)
    public void requestBodySensorPermission() {
        if(!EasyPermissions.hasPermissions(this, ACCELEROMETER)) {
            EasyPermissions.requestPermissions(this, "Please grant the body sensor permission", REQUEST_BODY_SENSOR, ACCELEROMETER);
        }
    }

    /**
     * Will show a toast to the user
     * @param msg that will be show to the user
     */
    public void showToastToUser(String msg){
        this.runOnUiThread(() -> {
            try{
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}