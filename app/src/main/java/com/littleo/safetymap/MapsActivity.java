package com.littleo.safetymap;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    Circle redCircle;
    Circle greenCircle;
    Circle blueCircle;
    Circle yellowCircle;

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
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        redCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(13.130155, 80.213897))
                .radius(50)
                .strokeColor(Color.RED));
        greenCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(13.129272, 80.214997))
                .radius(50)
                .strokeColor(Color.GREEN));
        blueCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(13.128549, 80.216083))
                .radius(50)
                .strokeColor(Color.BLUE));
        yellowCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(13.127848, 80.216972))
                .radius(50)
                .strokeColor(Color.YELLOW));
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
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

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
            TextView tvLatLng = (TextView) findViewById(R.id.textview2);
            tvLatLng.setText(mapMarker.getPosition().latitude + "\n" + mapMarker.getPosition().longitude);
            if (mCurrentLocation != null) {
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        getApplicationContext(), new GeocoderHandler());
            }
            float[] calDistR = new float[2];
            float[] calDistG = new float[2];
            float[] calDistB = new float[2];
            float[] calDistY = new float[2];
            Location.distanceBetween(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude, redCircle.getCenter().latitude, redCircle.getCenter().longitude,calDistR);
            Location.distanceBetween(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude, greenCircle.getCenter().latitude, greenCircle.getCenter().longitude,calDistG);
            Location.distanceBetween(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude, blueCircle.getCenter().latitude, blueCircle.getCenter().longitude,calDistB);
            Location.distanceBetween(mapMarker.getPosition().latitude, mapMarker.getPosition().longitude, yellowCircle.getCenter().latitude, yellowCircle.getCenter().longitude,calDistY);
            TextView region = (TextView) findViewById(R.id.textview3);
            if(calDistR[0]<redCircle.getRadius()){
                Toast.makeText(getBaseContext(), "RED", Toast.LENGTH_LONG).show();
                region.setText("RED");
            }
            if(calDistG[0]<greenCircle.getRadius()){
                Toast.makeText(getBaseContext(), "GREEN", Toast.LENGTH_LONG).show();
                region.setText("GREEN");
            }
            if(calDistB[0]<blueCircle.getRadius()){
                Toast.makeText(getBaseContext(), "BLUE", Toast.LENGTH_LONG).show();
                region.setText("BLUE");
            }
            if(calDistY[0]<yellowCircle.getRadius()){
                Toast.makeText(getBaseContext(), "YELLOW", Toast.LENGTH_LONG).show();
                region.setText("YELLOW");
            }
            /*CameraPosition cameraPosition = new CameraPosition.Builder()
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
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
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        if (marker != null) {
            TextView tvLatLng = (TextView) findViewById(R.id.textview2);
            tvLatLng.setText(marker.getPosition().latitude+"\n"+marker.getPosition().longitude);
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(marker.getPosition().latitude, marker.getPosition().longitude,
                    getApplicationContext(), new GeocoderHandler());
        }
        float[] calDistR = new float[2];
        float[] calDistG = new float[2];
        float[] calDistB = new float[2];
        float[] calDistY = new float[2];
        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, redCircle.getCenter().latitude, redCircle.getCenter().longitude,calDistR);
        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, greenCircle.getCenter().latitude, greenCircle.getCenter().longitude,calDistG);
        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, blueCircle.getCenter().latitude, blueCircle.getCenter().longitude,calDistB);
        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, yellowCircle.getCenter().latitude, yellowCircle.getCenter().longitude,calDistY);
        TextView region = (TextView) findViewById(R.id.textview3);
        if(calDistR[0]<redCircle.getRadius()){
            Toast.makeText(getBaseContext(), "RED", Toast.LENGTH_LONG).show();
            region.setText("RED");
        }
        if(calDistG[0]<greenCircle.getRadius()){
            Toast.makeText(getBaseContext(), "GREEN", Toast.LENGTH_LONG).show();
            region.setText("GREEN");
        }
        if(calDistB[0]<blueCircle.getRadius()){
            Toast.makeText(getBaseContext(), "BLUE", Toast.LENGTH_LONG).show();
            region.setText("BLUE");
        }
        if(calDistY[0]<yellowCircle.getRadius()){
            Toast.makeText(getBaseContext(), "YELLOW", Toast.LENGTH_LONG).show();
            region.setText("YELLOW");
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
        }
    }
}
