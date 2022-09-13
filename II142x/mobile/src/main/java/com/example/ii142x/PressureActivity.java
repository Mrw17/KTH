package com.example.ii142x;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.*;
import communication.AssetKeys;
import communication.MessagePath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PressureActivity extends Activity implements DataClient.OnDataChangedListener{
    TextView textViewCurrValue;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
    }

    /**
     * When user open activity it will
     * add listener on DataClient
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpGUI();
        setUpListeners();
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
     * Set up default GUI
     */
    private void setUpGUI(){
        textViewCurrValue = findViewById(R.id.textViewPressureCurrentValue);
        btnBack = findViewById(R.id.btnPressureBack);
        btnBack.setOnClickListener(v-> btnBackPressed());
    }

    /**
     * Start listeners
     */
    private void setUpListeners() {
        Wearable.getDataClient(this).addListener(this);
    }

    /**
     * Stop listeners
     */
    private void removeListeners(){
        Wearable.getDataClient(this).removeListener(this);
    }

    /**
     * Closes activity
     */
    private void btnBackPressed(){
        finish();
    }

    /**
     * If it receives any new data it will check for right
     * path and update GUI
     * @param dataEventBuffer with new data
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (MessagePath.PRESSURE.equals(event.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString(AssetKeys.PRESSURE);
                    onNewPressure(message);
                }
            }
        }
    }


    /**
     * Updates textview with new pressure
     * @param newPressure the new pressure
     */
    @UiThread
    @SuppressLint("SetTextI18n")
    private void onNewPressure(String newPressure){
        this.runOnUiThread(() -> {
            try{
                textViewCurrValue.setText(newPressure);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

}