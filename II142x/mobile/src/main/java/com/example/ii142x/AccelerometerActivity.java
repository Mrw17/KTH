package com.example.ii142x;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.annotation.UiThread;
import com.example.ii142x.DTO.AccelerometerDTO;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.*;
import communication.AssetKeys;
import communication.MessagePath;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Class that is responsible for accelerometer-data
 * It will receive accelerometer-data from another node and
 * display it to the user
 */
public class AccelerometerActivity extends Activity implements DataClient.OnDataChangedListener{
    TextView currAcc_1;
    TextView currAcc_2;
    TextView currAcc_3;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
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
     * Sets up default UI
     */
    private void setUpGUI() {
        currAcc_1 = findViewById(R.id.textViewCurrAccelerometer1);
        currAcc_2 = findViewById(R.id.textViewCurrAccelerometer2);
        currAcc_3 = findViewById(R.id.textViewCurrAccelerometer3);

        btnBack = findViewById(R.id.btnAccelerometerBack);
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
    private void btnBackPressed() {
        finish();
    }


    /**
     * When a new data event happens aka new object is coming
     * @param dataEventBuffer with the new data
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        // Go through all events
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (MessagePath.ACCELEROMETER.equals(event.getDataItem().getUri().getPath())) {

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset asset = dataMapItem.getDataMap().getAsset(AssetKeys.ACCELERATOR);

                    // Updates GUI with new accelerometerDTO
                    new Thread(() -> {
                        AccelerometerDTO accelerometerDTO = loadBitmapFromAsset(asset);
                        onNewAccelerometerData(accelerometerDTO);
                    }).start();
                }
            }
        }
    }

    /**
     * Updates textview with new pressure
     * @param accelerometerDTO the new accelerometer
     */
    @UiThread
    @SuppressLint("SetTextI18n")
    private void onNewAccelerometerData(AccelerometerDTO accelerometerDTO){
        this.runOnUiThread(() -> {
            try{
                if(accelerometerDTO == null)
                    return;

                System.out.println(accelerometerDTO.getX() + "aaa");
                currAcc_1.setText(Double.toString(accelerometerDTO.getX()));
                currAcc_2.setText(Double.toString(accelerometerDTO.getY()));
                currAcc_3.setText((Double.toString(accelerometerDTO.getZ())));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * Creates a bitmap from asset
     * @param asset to be converted
     * @return Bitmap that has been converted
     */
    private AccelerometerDTO loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        try{
            // convert asset
            InputStream assetInputStream =
                    Tasks.await(Wearable.getDataClient(this.getBaseContext()).getFdForAsset(asset))
                            .getInputStream();

            // decode the stream to a bitmap

            ObjectInputStream objectInputStream
                    = new ObjectInputStream(assetInputStream);
            AccelerometerDTO accelerometerDTO = (AccelerometerDTO) objectInputStream.readObject();

            objectInputStream.close();

            return accelerometerDTO;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}