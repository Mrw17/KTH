package com.example.ii142x;

import android.Manifest;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.example.ii142x.communication.MessagePath;
import communication.SendMessage;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

public class HeartRateActivity extends Activity implements SensorEventListener, EasyPermissions.PermissionCallbacks {

    private TextView textViewHeartRate;
    private Button btnHeartRate;
    private Button btnBackToMainActivity;
    SensorManager sensorManager;
    Sensor heartRateSensor;
    private static final String[] BODY_SENSOR = {Manifest.permission.BODY_SENSORS};
    private final int REQUEST_BODY_SENSOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        sensorManager = (SensorManager) getBaseContext().getSystemService(SENSOR_SERVICE);
        setUpGUI();
    }

    /**
     * When user open activity it will reset GUI
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
        sensorManager.unregisterListener(this);
    }


    /**
     * Get references to UI elements
     * Set up listeners on buttons
     */
    private void setUpGUI() {
        btnHeartRate = findViewById(R.id.btnStartHeartRate);
        btnHeartRate.setOnClickListener(v-> readHeartRateBtnPressed());

        btnBackToMainActivity = findViewById(R.id.btnBackMainActivity);
        btnBackToMainActivity.setOnClickListener(v-> sendUserToMainActivity());

        textViewHeartRate = findViewById(R.id.textViewHeartRate);
    }

    /**
     * It will request permission to body sensor
     * after it will start/stop read heart rate
     */
    private void readHeartRateBtnPressed() {
        requestBodySensorPermission();

        if(btnHeartRate.getText().equals(getString(R.string.btnStartHeartRate))){
            startReadingHeartRate();
            btnHeartRate.setText(R.string.btnStopHeartRate);
        }

        else{
            stopReadingHeartRate();
            btnHeartRate.setText(R.string.btnStartHeartRate);
        }
    }

    /**
     * It will start to read heart rate
     * by adding a listener on BODY_SENSOR
     * with normal delay
     * if it has permissions to do it
     */
    private void startReadingHeartRate() {
        try{
            if (hasHeartRatePermissions()) {
                registerHeartRateListener();

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerHeartRateListener(){
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this,
                heartRateSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Stop reading heart rate by unregister listener
     */
    private void stopReadingHeartRate(){
        sensorManager.unregisterListener(this);
        heartRateSensor = null;
    }

    /**
     * Send user back to MainActivity
     */
    private void sendUserToMainActivity() {
        finish();
    }

    /**
     * Updates GUI with new sensor value
     * Sends the new value to mobile
     * @param sensorEvent event with new sensor value
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try{
            if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                updateTextViewHeartRate(String.valueOf(sensorEvent.values[0]));
                sendToMobile(sensorEvent.values[0]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Notify user if accuracy is changed on a sensor
     * @param sensor that was changed
     * @param i new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        this.runOnUiThread(() -> {
            try{
                String msg = "Accuracy Changed on heart rate sensor: " + sensor.getType();
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates textview with new values
     * @param newText that will be shown to user
     */
    private void updateTextViewHeartRate(String newText){
        textViewHeartRate.setText(newText);
    }


    private void sendToMobile(float value){

        try{
            SendMessage sendMessage = new SendMessage(MessagePath.HEART_RATE, Float.toString(value), this);
            sendMessage.start();
        }
        catch (Exception e){
            showToastToUser("Could not send message");
            e.printStackTrace();
        }
    }

    private void requestBodySensorPermission() {
        if(EasyPermissions.hasPermissions(this, BODY_SENSOR)) {
            showToastToUser("Permission already granted");
        }
        else {
            this.runOnUiThread(() -> {
                try{
                    askForPermissions(BODY_SENSOR);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     *
     * @return status on heart rate permissions
     */
    private boolean hasHeartRatePermissions() {
        return (EasyPermissions.hasPermissions(this, BODY_SENSOR));
    }

    /**
     * will ask for permissions
     * @param permissions to be asked for
     */
    private void askForPermissions(String[] permissions){
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.permissions_ask_for_heart_rate),
                REQUEST_BODY_SENSOR,
                permissions);
    }

    /**
     * If user grants permissions
     * @param requestCode request code for tracking
     * @param perms permissions that was asked for
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startReadingHeartRate();
        showToastToUser("Permissions granted");
    }


    /**
     * If user denies permissions
     * @param requestCode request code for tracking
     * @param perms permissions that was asked for
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToastToUser("Need permission to body sensor to be able to read heart rate");
    }

    /**
     * Callback for the result of requesting permissions
     * @param requestCode request code for tracking
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Let EasyPermissions handle the result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

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