package com.example.ii142x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.ii142x.communication.MessagePath;
import com.example.ii142x.communication.SendMessage;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import org.jetbrains.annotations.NotNull;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

public class GPSActivity extends Activity implements LocationListener, EasyPermissions.PermissionCallbacks {

    private Button btnGps;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private Button btnBack;
    private Location location;
    private LocationManager locationManager;
    private String provider;
    int coordinateDecimals = 5;

    private static final int REQUEST_LOCATION = 3;


    private static final String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        setUpGUI();
    }


    private void setUpGUI() {
        btnGps = findViewById(R.id.btnGpsGetGPS);
        btnGps.setOnClickListener(v -> getCurrentGPS());

        btnBack = findViewById(R.id.btnBackMainActivity);
        btnBack.setOnClickListener(v -> sendUserToMainActivity());

        textViewLatitude = findViewById(R.id.textViewGpsLatitude);
        textViewLongitude = findViewById(R.id.textViewGpsLongitude);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentGPS() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (hasLocationPermissions()) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                provider = locationManager.getBestProvider(criteria, false);
                location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    onLocationChanged(location);
                }

            } else {
                askForPermissions();
            }
        }, 1000);
        new Thread(() -> {
        });
    }

    private void sendToMobile(){

        try{
            SendMessage sendMessage = new SendMessage(MessagePath.GPS, getLatitude(location) + ", " + getLongitude(location), this);
            sendMessage.start();
        }
        catch (Exception e){
            showToastToUser("Could not send message");
            e.printStackTrace();
        }
    }

    private void askForPermissions() {
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.askForGPSPermissions),
                REQUEST_LOCATION,
                LOCATION_PERMISSIONS);
    }


    /**
     * Closes the activity
     */
    private void sendUserToMainActivity() {
        finish();
    }

    /**
     * Will show a toast to the user
     *
     * @param msg that will be show to the user
     */
    public void showToastToUser(String msg) {
        this.runOnUiThread(() -> {
            try {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void updateGUI() {
        if (location != null) {
            textViewLongitude.setText(getLongitude(location));
            textViewLatitude.setText(getLatitude(location));
        }
    }

    private String getLongitude(Location location) {
        String longitude = Double.toString(location.getLongitude());

        String[] temp = longitude.split("\\.");
        String a = temp[1];

        return temp[0] + "." + a.substring(0, Math.min(a.length(), coordinateDecimals));
        //return longitude.substring(0, Math.min(longitude.length(), 3 + coordinateDecimals));
    }

    private String getLatitude(Location location) {
        String latitude = Double.toString(location.getLatitude());
        return latitude.substring(0, Math.min(latitude.length(), 3 + coordinateDecimals));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        updateGUI();
        sendToMobile();
    }

    /**
     * If user grants permissions
     *
     * @param requestCode request code for tracking
     * @param perms       permissions that was asked for
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getCurrentGPS();
        showToastToUser("Permissions granted");
    }


    /**
     * If user denies permissions
     *
     * @param requestCode request code for tracking
     * @param perms       permissions that was asked for
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showToastToUser("Need permissions to read GPS");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Let EasyPermissions handle the result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    private boolean hasLocationPermissions() {
        return (EasyPermissions.hasPermissions(this, LOCATION_PERMISSIONS));
    }
}
