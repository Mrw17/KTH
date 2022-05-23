package com.example.ii142x;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.ii142x.DTO.AccelerometerDTO;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import communication.MessageBundles;
import communication.MessagePath;

/**
 * Class that is responsible for GPS-coordinates
 * It will receive GPS-coordinate from a another node and
 * display it to the user
 */
public class GPSActivity extends Activity  implements MessageClient.OnMessageReceivedListener{
    TextView textViewLongitude;
    TextView textViewLatitude;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

    }


    /**
     * When user open activity it will
     * add required listeners
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpGUI();
        setUpListeners();
    }

    /**
     * When user pause the application it will
     * remove listeners
     */
    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    /**
     * Set up default GUI
     */
    private void setUpGUI(){
        textViewLongitude = findViewById(R.id.textViewGPSCurrLongitude);
        textViewLatitude = findViewById(R.id.textViewGPSCurrLatitude);
        btnBack = findViewById(R.id.btnGPSBack);
        btnBack.setOnClickListener(v-> backBtnPressed());
    }

    /**
     * Register listener for a message receiver
     */
    private void setUpListeners(){
       // LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
         //       new IntentFilter(Intent.ACTION_SEND));
        Wearable.getMessageClient(this).addListener(this);

    }

    /**
     * Removes listeners
     */
    private void removeListeners(){
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        Wearable.getMessageClient(this).removeListener(this);
    }

    /**
     * When a new message is received it will update GUI
     * @param newCoordinates new coordinates
     */
    @SuppressLint("SetTextI18n")
    private void newMessageReceived(String newCoordinates){
        String latitude = getLatitude(newCoordinates);
        String longitude = getLongitude(newCoordinates);

        textViewLongitude.setText(latitude);
        textViewLatitude.setText(longitude);
    }

    /**
     * Will extract longitude from coordinate string
     * @param newCoordinates new coordinates
     * @return longitude part of coordinates
     */
    private String getLongitude(String newCoordinates) {
        String[] parts = newCoordinates.split(",");
        return parts[0];
    }

    /**
     * Will extract latitude from coordinate string
     * @param newCoordinates new coordinates
     * @return latitude part of coordinates
     */
    private String getLatitude(String newCoordinates) {
        String[] parts = newCoordinates.split(",");
        return parts[1];
    }

    /**
     * Receiver that will listen on new messages
     * and update the gui with new data
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Gets the message
            Bundle bundle = intent.getExtras();
           // String onMessageReceived = (String) bundle.get(MessageBundles.GPS);
            AccelerometerDTO test = (AccelerometerDTO) bundle.get(MessageBundles.GPS);

            if(test != null)
                newMessageReceived(test.getX() + " " + test.getY() + " " + test.getZ());
        }
    };

    /**
     * Closes the activity
     */
    private void backBtnPressed() {
        finish();
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if(messageEvent.getPath().equals(MessagePath.GPS)){
            byte[] bytes = messageEvent.getData();
            String output = new String(bytes);
            newMessageReceived(output);
            //update.setText(output);
        }
    }
}