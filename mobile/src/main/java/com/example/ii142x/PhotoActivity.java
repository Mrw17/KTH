package com.example.ii142x;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.*;
import communication.AssetKeys;
import communication.MessagePath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity implements DataClient.OnDataChangedListener{
    private Button btnSendPhoto;
    private Button btnReset;
    private Button btnBack;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setUpGUI();
    }

    /**
     * When user open activity it will
     * add listener on DataClient
     */
    @Override
    public void onResume() {
        super.onResume();
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
     * Default GUI
     */
    private void setUpGUI() {
        btnSendPhoto = findViewById(R.id.btnPhotoSend);
        btnSendPhoto.setOnClickListener(v-> sendPhotoBtnPressed());

        btnReset = findViewById(R.id.btnPhotoReset);
        btnReset.setOnClickListener(v -> resetImageBtnPressed());

        btnBack = findViewById(R.id.btnPhotoBack);
        btnBack.setOnClickListener(v-> backBtnPressed());

        imgView = findViewById(R.id.imgView);
    }

    /**
     * Closes the activity
     */
    private void backBtnPressed() {
        finish();
    }

    /**
     * Resets the image to start image
     */
    private void resetImageBtnPressed() {
        imgView.setImageResource(0);
        imgView.setImageResource(R.drawable.pic);
    }

    /**
     * When a new DataItem is received
     * @param dataEventBuffer with all new events
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (MessagePath.PHOTO.equals(event.getDataItem().getUri().getPath())) {
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
     * When user press the button
     * it will send photo
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
     * Sending an asset with DataClient
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
     * Converts a bitmap to asset
     * @param bitmap to be converted
     * @return the converted asset
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
                    // ignore
                }
            }
        }
    }

    /**
     * Creates a bitmap from asset
     * @param asset to be converted
     * @return Bitmap that has been converted
     */
    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        try{
            // convert asset
            InputStream assetInputStream =
                    Tasks.await(Wearable.getDataClient(this.getBaseContext()).getFdForAsset(asset))
                            .getInputStream();

            // decode the stream to a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the GUI with new image
     * @param bitmap new image
     */
    @UiThread
    private void updateUi(Bitmap bitmap){
        runOnUiThread(() -> {
            if (bitmap != null) {
                imgView.setImageResource(0);
                imgView.setImageBitmap(bitmap);
            }
        });
    }
}