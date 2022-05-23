package com.example.ii142x;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class HeartRateActivity extends AppCompatActivity {

    private Button btnBack;
    private TextView textViewCurrHeartBeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        setUpGUI();
        setUpListeners();

    }

    /**
     * Set up default ui
     */
    private void setUpGUI(){
        btnBack = findViewById(R.id.btnBackMainActivity);
        btnBack.setOnClickListener(v-> backBtnPressed());

        textViewCurrHeartBeat = findViewById(R.id.textViewHeartRateCurrentValue);
    }

    /**
     * Register listener for a message receiver
     */
    private void setUpListeners(){
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Intent.ACTION_SEND));
    }

    /**
     * Send user back to MainActivity
     */
    private void backBtnPressed() {
        finish();
    }

    /**
     * When a new message is received it will update GUI
     * @param newMsg message that aws received
     */
    private void newMessageReceived(String newMsg){
        textViewCurrHeartBeat.setText(newMsg);
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
            String onMessageReceived = (String) bundle.get("heartRate");
            if(onMessageReceived != null)
                newMessageReceived(onMessageReceived);
        }
    };
}