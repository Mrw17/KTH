package com.example.ii142x;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.ii142x.communication.AssetKeys;
import com.example.ii142x.communication.MessagePath;
import com.example.ii142x.databinding.ActivityPhotoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class PhotoActivity extends Activity implements DataClient.OnDataChangedListener{
    private Button btnSendPhoto;
    private Button btnReset;
    private Button btnBack;
    private ActivityPhotoBinding binding;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private void setUpGUI() {
        btnSendPhoto = findViewById(R.id.btnImageSend);
        btnSendPhoto.setOnClickListener(v-> sendPhotoBtnPressed());

        btnReset = findViewById(R.id.btnImageReset);
        btnReset.setOnClickListener(v -> resetImageBtnPressed());

        btnBack = findViewById(R.id.btnPhotoBack);
        btnBack.setOnClickListener(v -> backBtnPressed());

        imgView = findViewById(R.id.imgView);

    }

    private void sendPhotoBtnPressed(){
        new Thread(() -> {
            Bitmap mImageBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.pic);
            Asset asset = toAsset(mImageBitmap);
            sendPhoto(asset);
        }).start();
    }

    private void backBtnPressed() {
        finish();
    }


    private void resetImageBtnPressed() {
        imgView.setImageResource(0);
        imgView.setImageResource(R.drawable.pic);
    }

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

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        System.out.println("onDataChanged: " + dataEventBuffer);
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


    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }


        try{
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream =
                    Tasks.await(Wearable.getDataClient(this.getBaseContext()).getFdForAsset(asset))
                            .getInputStream();



            if (assetInputStream == null) {
                System.out.println("Requested an unknown Asset.");
                return null;
            }
            // decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }
        catch (Exception e){
            System.out.println(e.toString());
            return null;
        }

    }

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


    private void updateUi(Bitmap bitmap){
        runOnUiThread(() -> {
            if (bitmap != null) {
                imgView.setImageResource(0);
                imgView.setImageBitmap(bitmap);
            }
        });
    }


}