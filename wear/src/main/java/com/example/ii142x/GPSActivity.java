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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.ii142x.DTO.GpsDTO;
import com.example.ii142x.communication.MessagePath;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Class that will read GPS and sent it to mobile
 */
public class GPSActivity extends Activity implements LocationListener, EasyPermissions.PermissionCallbacks {

    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private Location location;
    private LocationManager locationManager;
    private String provider;
    int coordinateDecimals = 5;
    private static final int REQUEST_LOCATION = 3;

    private static final String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    /**
     * Set up default GUI and get locationManager from system
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        setUpGUI();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Default GUI and button listeners
     */
    private void setUpGUI() {
        Button btnGps = findViewById(R.id.btnGpsGetGPS);
        btnGps.setOnClickListener(v -> getCurrentGPS());

        Button btnBack = findViewById(R.id.btnBackMainActivity);
        btnBack.setOnClickListener(v -> sendUserToMainActivity());

        textViewLatitude = findViewById(R.id.textViewGpsLatitude);
        textViewLongitude = findViewById(R.id.textViewGpsLongitude);
    }

    /**
     * Gets current GPS
     */
    @SuppressLint("MissingPermission")
    private void getCurrentGPS() {
        if (hasLocationPermissions()) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);

            if (location != null)
                onLocationChanged(location);

            } else
                askForPermissions();
    }

    /**
     * Ask the user for permission to location
     */
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


    /**
     * Updates GUI with new coordinates
     */
    private void updateGUI() {
        if (location != null) {
            textViewLongitude.setText(getLongitude(location));
            textViewLatitude.setText(getLatitude(location));
        }
    }

    /**
     * Gets longitude and format it
     * @param location that longitude will be extracted from
     * @return longitude coordinates
     */
    private String getLongitude(Location location) {
        String longitude = Double.toString(location.getLongitude());
        String[] temp = longitude.split("\\.");
        String a = temp[1];
        return temp[0] + "." + a.substring(0, Math.min(a.length(), coordinateDecimals));
    }

    /**
     * Gets latitude and format it
     * @param location that latitude will be extracted from
     * @return latitude coordinates
     */
    private String getLatitude(Location location) {
        String latitude = Double.toString(location.getLatitude());
        return latitude.substring(0, Math.min(latitude.length(), 3 + coordinateDecimals));
    }

    /**
     * When sensor sends location changed it will
     * update GUI and send it mobile
     * @param location new location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        updateGUI();
        sendGpsToPhone(new GpsDTO(location.getLatitude(), location.getLongitude()));
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

    /**
     * Reult on permisson request
     * @param requestCode The request code for tracking
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
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
     *
     * @return status on permissions
     */
    private boolean hasLocationPermissions() {
        return (EasyPermissions.hasPermissions(this, LOCATION_PERMISSIONS));
    }

    /**
     * Sends GPS to the phone through MessageClient
     * @param gpsDTO to be sent
     */
    private void sendGpsToPhone(GpsDTO gpsDTO) {
        new Thread(() -> {
            try {
                Collection<String> nodes = getNodes();
                Wearable.getMessageClient(getBaseContext()).sendMessage(nodes.iterator().next(), MessagePath.GPS, gpsDTO.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     *
     * @return all nodes that are available
     */
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes)
                results.add(node.getId());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return results;
    }
}