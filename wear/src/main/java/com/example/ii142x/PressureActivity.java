package com.example.ii142x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.ii142x.communication.AssetKeys;
import com.example.ii142x.communication.MessagePath;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.*;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PressureActivity extends Activity implements
        EasyPermissions.PermissionCallbacks, SensorEventListener {

    private Button btnReadPressure;
    private TextView textViewCurrPressure;
    private Button btnBack;
    SensorManager mSensorManager;
    double currentPressure = 0;

    private static final int REQUEST_BODY_SENSOR = 3;
    private static final String[] BODY_SENSOR =
            {Manifest.permission.BODY_SENSORS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        mSensorManager = (SensorManager) getBaseContext().getSystemService(SENSOR_SERVICE);
        setUpGUI();
    }

    private void setUpGUI(){
        btnReadPressure = findViewById(R.id.btnGetPressure);
        btnReadPressure.setOnClickListener(v-> getPressureBtnPressed());

        btnBack = findViewById(R.id.btnPressureBack);
        btnBack.setOnClickListener(v-> sendUserBackToMainActivity());

        textViewCurrPressure = findViewById(R.id.textViewPressureCurrentValue);
    }

    private void sendUserBackToMainActivity() {
        finish();
    }

    /**
     * When user open activity it will
     * add listener on DataClient
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpGUI();
    }

    /**
     * When user pause the application it will
     * remove listener on dataClient
     */
    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    /**
     * Removes listeners
     */
    private void removeListeners(){
        mSensorManager.unregisterListener(this);
    }

    /**
     * When user press pressure-button
     * it will check for start/stop and
     * start/stop to reading
     */
    private void getPressureBtnPressed() {
        requestBodySensorPermission();

        if(btnReadPressure.getText().equals(getString(R.string.btnPressureStart))){
            startReadingPressure();
            btnReadPressure.setText(R.string.btnPressureStop);
        }
        else{
            stopReadingPressure();
            btnReadPressure.setText(R.string.btnPressureStart);
        }
    }

    /**
     * Stop reading pressure by
     * removing listener
     */
    private void stopReadingPressure() {
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    /**
     * Checks for permission and start reading
     * pressure-sensor
     */
    private void startReadingPressure() {
        if (hasPressurePermissions()) {
            Sensor pressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mSensorManager.registerListener(this,
                    pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
                removeListeners();
        }
    }

    /**
     * Request permission if they misses
     */
    @AfterPermissionGranted(REQUEST_BODY_SENSOR)
    public void requestBodySensorPermission() {
        String[] perms = {Manifest.permission.BODY_SENSORS};
        if(EasyPermissions.hasPermissions(this, perms))
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        else
            EasyPermissions.requestPermissions(this, "Please grant the body sensor permission", REQUEST_BODY_SENSOR, perms);
    }

    /**
     * If user grant permissions
     * @param requestCode to track sensor
     * @param perms that has been granted
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startReadingPressure();
    }

    /**
     * If user decline permissions
     * @param requestCode to track sensor
     * @param perms to have been asked for
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * Result of permissions request, sends it to
     * EasyPermissions
     * @param requestCode The request code
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results of request
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
     * Returns status of pressure permissions
     * @return status of pressure permissions
     */
    private boolean hasPressurePermissions() {
        return (EasyPermissions.hasPermissions(this, BODY_SENSOR));
    }

    /**
     * If the sensor value is changed it will
     * update GUI and send it to the mobile
     * @param sensorEvent the event that happend on the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
                currentPressure = sensorEvent.values[0];
                updateTextViewPressure();
                sendData(Double.toString(currentPressure));
        }
    }

    /**
     * Sends out the data as a DataItem
     * @param pressure to be sent
     */
    private void sendData(String pressure) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(MessagePath.PRESSURE);
        dataMap.getDataMap().putString(AssetKeys.PRESSURE, pressure);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask.addOnSuccessListener(
                dataItem -> System.out.println("Sending image was successful: " + dataItem));
    }

    /**
     * Updates textview with new pressure
     */
    @UiThread
    @SuppressLint("SetTextI18n")
    private void updateTextViewPressure(){
        this.runOnUiThread(() -> {
            try{
                textViewCurrPressure.setText(Double.toString(currentPressure));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * If accuracy changed on sensor
     * @param sensor to be watching
     * @param i sensor id
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}