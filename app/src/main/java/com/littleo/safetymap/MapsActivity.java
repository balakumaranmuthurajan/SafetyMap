package com.littleo.safetymap;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import java.text.DateFormat;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMarkerDragListener {

    private static final String TAG = "MapsActivity";
    private static final long INTERVAL = 1000 * 5 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 1 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    GoogleMap googleMap;
    Marker mapMarker;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ");
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_maps);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mapMarker.setDraggable(true);
        //googleMap.setOnMarkerDragListener(this);
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().tilt(45).build()));
        //CameraPosition cameraPosition = new CameraPosition.Builder().tilt(45).bearing(90).build();
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //googleMap.animateCamera(CameraUpdateFactory.);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected : " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected : " + mGoogleApiClient.isConnected());
        MarkerOptions options = new MarkerOptions();
        //Location lastLocation=null;
        //while(lastLocation==null)
        //lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng currentLatLng = new LatLng(0, 0);
        options.position(currentLatLng);
        mapMarker = googleMap.addMarker(options);
        mapMarker.setDraggable(true);
        /*mCurrentLocation = googleMap.getMyLocation();
        if (mCurrentLocation != null) {
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                    getApplicationContext(), new GeocoderHandler());
        }*/
        googleMap.setOnMarkerDragListener(this);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        Log.d(TAG, "Location update started : ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mapMarker != null) {
            mapMarker.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            long atTime = mCurrentLocation.getTime();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
            mapMarker.setTitle("Bala");
            //mapMarker.setSnippet(mLastUpdateTime + " " + mCurrentLocation.toString());
            mapMarker.setDraggable(true);
            //mapMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_play));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
            Log.d(TAG, "Marker added");
            if (mCurrentLocation != null) {
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        getApplicationContext(), new GeocoderHandler());
            }
        }
        else {
            onConnected(Bundle.EMPTY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapMarker.remove();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed ");
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "Drag fired ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "in drag ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "Drag stopped ");
        //Location dragLocation;
        //dragLocation = null;
        //dragLocation.setLatitude(marker.getPosition().latitude);
        //dragLocation.setLongitude(marker.getPosition().longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
        if (marker != null) {
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(marker.getPosition().latitude, marker.getPosition().longitude,
                    getApplicationContext(), new GeocoderHandler());
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            TextView tvLocation = (TextView) findViewById(R.id.textview1);
            tvLocation.setText(locationAddress);
            //
        }
    }
}
