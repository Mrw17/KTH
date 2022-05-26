package com.example.ii142x;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.example.ii142x.DTO.GpsDTO;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import communication.MessagePath;

/**
 * Class that is responsible for GPS-coordinates
 * It will receive GPS-coordinate from another node and
 * display it to the user
 */
public class GPSActivity extends Activity  implements MessageClient.OnMessageReceivedListener{
    TextView textViewLongitude;
    TextView textViewLatitude;
    Button btnBack;
    int coordinateDecimals = 5;

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
        Wearable.getMessageClient(this).addListener(this);
    }

    /**
     * Removes listeners
     */
    private void removeListeners(){
        Wearable.getMessageClient(this).removeListener(this);
    }

    /**
     * When a new message is received it will update GUI
     * @param gpsDTO new coordinates
     */
    @SuppressLint("SetTextI18n")
    private void newMessageReceived(GpsDTO gpsDTO){
        String latitude = Double.toString(gpsDTO.getLatitude());
        latitude =  latitude.substring(0, Math.min(latitude.length(), 3 + coordinateDecimals));

        String longitude = Double.toString(gpsDTO.getLongitude());
        longitude =  longitude.substring(0, Math.min(longitude.length(), 3 + coordinateDecimals));

        updateGUIWithNewCoordinates(latitude, longitude);
    }

    @UiThread
    private void updateGUIWithNewCoordinates(String latitude, String longitude) {
        this.runOnUiThread(() -> {
            try{
                textViewLatitude.setText(latitude);
                textViewLongitude.setText(longitude);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

    }


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
            try{
                GpsDTO gpsDTO = GpsDTO.deserialize(bytes);
                newMessageReceived(gpsDTO);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}