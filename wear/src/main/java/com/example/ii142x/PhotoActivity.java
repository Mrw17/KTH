package com.example.ii142x;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.example.ii142x.communication.AssetKeys;
import com.example.ii142x.communication.MessagePath;
import com.example.ii142x.databinding.ActivityPhotoBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Class that will send/receive a photo from mobile
 */
public class PhotoActivity extends Activity implements DataClient.OnDataChangedListener{
    private ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.ii142x.databinding.ActivityPhotoBinding binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * When user open activity it will
     * set upp default GUI and
     * add listener on DataClient
     */
    @Override
    public void onResume() {
        super.onResume();
        setUpGUI();
        Wearable.getDataClient(this).addListener(this);
    }

    /**
     * When user pause the application it will
     * remove listener on dataClient
     */
    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    /**
     * Default GUI and listeners on buttons
     */
    private void setUpGUI() {
        Button btnSendPhoto = findViewById(R.id.btnImageSend);
        btnSendPhoto.setOnClickListener(v-> sendPhotoBtnPressed());

        Button btnReset = findViewById(R.id.btnImageReset);
        btnReset.setOnClickListener(v -> resetImageBtnPressed());

        Button btnBack = findViewById(R.id.btnPhotoBack);
        btnBack.setOnClickListener(v -> backBtnPressed());

        imgView = findViewById(R.id.imgView);

    }

    /**
     * When user press send photo it will
     * create a new thread and send it mobile
     */
    private void sendPhotoBtnPressed(){
        new Thread(() -> {
            Bitmap mImageBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.pic);
            Asset asset = toAsset(mImageBitmap);
            sendPhoto(asset);
        }).start();
    }

    /**
     * When user press back-button it
     * will close activity
     */
    private void backBtnPressed() {
        finish();
    }


    /**
     * Resets image to default
     */
    private void resetImageBtnPressed() {
        imgView.setImageResource(0);
        imgView.setImageResource(R.drawable.pic);
    }

    /**
     * Sends asset with DataClient to mobile with
     * urgent priority
     * @param asset to be sent
     */
    private void sendPhoto(Asset asset) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(MessagePath.PHOTO);
        dataMap.getDataMap().putAsset(AssetKeys.PHOTO, asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask.addOnSuccessListener(
                dataItem -> System.out.println("Sending image was successful: " + dataItem));
    }

    /**
     * If new data is received it will decode it to a bitmap and
     * show it to the user
     * @param dataEventBuffer with new data
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (MessagePath.PHOTO.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset asset = dataMapItem.getDataMap().getAsset(AssetKeys.PHOTO);
                    new Thread(() -> {
                        Bitmap bitmap = loadBitmapFromAsset(asset);
                        updateUi(bitmap);
                    }).start();
                }
            }
        }
    }

    /**
     * Loads a bitmap from an Asset
     * @param asset that contains bitmap
     * @return converted bitmap
     */
    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        try{
            // Gets input stream
            InputStream assetInputStream =
                    Tasks.await(Wearable.getDataClient(this.getBaseContext()).getFdForAsset(asset))
                            .getInputStream();

            // Decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Transform a bitmap tot asset
     * @param bitmap to be transformed to asset
     * @return an asset containing bitmap
     */
    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates GUI with new bitmap
     * @param bitmap to be shown to uer
     */
    private void updateUi(Bitmap bitmap){
        runOnUiThread(() -> {
            if (bitmap != null) {
                imgView.setImageResource(0);
                imgView.setImageBitmap(bitmap);
            }
        });
    }
}