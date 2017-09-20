package dienichtskoenner.stepbystep.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dienichtskoenner.stepbystep.Chart;
import dienichtskoenner.stepbystep.Constants;
import dienichtskoenner.stepbystep.R;

/**
 * Created by c.lisik on 01.09.2017.
 * A high part of this Code was used by a tutorial from https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
 */

public class GeoFenceMainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status> {


    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private MapFragment mapFragment;
    private LocationRequest locationRequest;
    private Marker locationMarker;
    private Marker geoFenceMarker;
    private PendingIntent geoFencePendingIntent;
    private Handler mHandler = new Handler();


    private TextView cancelText;
    private Button startGeofenceButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        initViews();
        initGMaps();
        createGoogleApi();
    }

    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, GeoFenceMainActivity.class );
        intent.putExtra(Constants.ConstantsGeoFenceMainActivity.NOTIFICATION_MSG, msg );
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.action_bar_chart:
                Intent intent = new Intent(this, Chart.class);
                startActivity(intent);
                break;
            case R.id.action_bar_geofence:break;

            default:return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void initViews(){

        cancelText=(TextView) findViewById(R.id.cancelTextView);
        startGeofenceButton=(Button) findViewById(R.id.startGeofenceButton);

        startGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(geoFenceMarker!=null){

                    startGeofenceButton.setEnabled(false);
                    startGeofenceButton.setAlpha(.5f);
                    startGeofence();

                    Intent startTimer=new Intent(GeoFenceMainActivity.this,TimerActivity.class);
                    startActivity(startTimer);

                }else{
                   Toast.makeText(getApplicationContext(),R.string.no_marker_found, Toast.LENGTH_LONG).show();
                }
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGeofenceButton.setEnabled(true);
                startGeofenceButton.setAlpha(1f);

                clearGeofence();
            }
        });
    }

    private void createGoogleApi() {
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        googleApiClient.disconnect();
    }

    private boolean checkPermission() {
        return (android.os.Build.VERSION.SDK_INT >= 17 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                Constants.ConstantsGeoFenceMainActivity.REQ_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case Constants.ConstantsGeoFenceMainActivity.REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){

                    getLastKnownLocation();

                } else {
                    permissionsDenied();
                }break;
            }
        }
    }

    private void permissionsDenied() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.ConstantsFragmentBottom.REQUEST_LOCATION);
    }

    private void initGMaps(){
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void startLocationUpdates(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Constants.ConstantsGeoFenceMainActivity.UPDATE_INTERVAL)
                .setFastestInterval(Constants.ConstantsGeoFenceMainActivity.FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        writeActualLocation(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
        recoverGeofenceMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void getLastKnownLocation() {
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                writeLastLocation();
                startLocationUpdates();
            } else {
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    private void writeActualLocation(Location location) {

        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }


    private void markerLocation(LatLng latLng) {
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( map!=null ) {
            if ( locationMarker != null )
                locationMarker.remove();

            locationMarker = map.addMarker(markerOptions);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, Constants.ConstantsGeoFenceMainActivity.GEOFENCE_ZOOM);
            map.animateCamera(cameraUpdate);
        }
    }

    private void markerForGeofence(LatLng latLng) {
        String title = latLng.latitude + ", " + latLng.longitude;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(title);
        if ( map!=null ) {

            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);

        }
    }

    private void startGeofence() {
        if( geoFenceMarker != null ) {
            Geofence geofence = createGeofence( geoFenceMarker.getPosition(), Constants.ConstantsGeoFenceMainActivity.GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
        }
    }

    private Geofence createGeofence( LatLng latLng, float radius ) {
        return new Geofence.Builder()
                .setRequestId(Constants.ConstantsGeoFenceMainActivity.GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( Constants.ConstantsGeoFenceMainActivity.GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, Constants.ConstantsGeoFenceMainActivity.GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    private void addGeofence(GeofencingRequest request) {
        if (checkPermission()){
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if ( status.isSuccess() ) {
            saveGeofence();
            drawGeofence();
        }
    }

    private Circle geoFenceLimits;
    private void drawGeofence() {

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( Constants.ConstantsGeoFenceMainActivity.GEOFENCE_RADIUS );
        geoFenceLimits = map.addCircle( circleOptions );
    }

    private void saveGeofence() {
        SharedPreferences sharedPref = getPreferences( Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LAT, Double.doubleToRawLongBits( geoFenceMarker.getPosition().latitude ));
        editor.putLong( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LON, Double.doubleToRawLongBits( geoFenceMarker.getPosition().longitude ));
        editor.apply();
    }

    private void recoverGeofenceMarker() {
        SharedPreferences sharedPref = getPreferences( Context.MODE_PRIVATE );

        if ( sharedPref.contains( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LAT ) && sharedPref.contains( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LON )) {
            double lat = Double.longBitsToDouble( sharedPref.getLong( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LAT, -1 ));
            double lon = Double.longBitsToDouble( sharedPref.getLong( Constants.ConstantsGeoFenceMainActivity.KEY_GEOFENCE_LON, -1 ));
            LatLng latLng = new LatLng( lat, lon );
            markerForGeofence(latLng);
            drawGeofence();
        }
    }

    private void clearGeofence() {
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                createGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if ( status.isSuccess() ) {

                    removeGeofenceDraw();
                    geoFenceMarker=null;
                }
            }
        });
    }

    private void removeGeofenceDraw() {
        if ( geoFenceMarker != null)
            geoFenceMarker.remove();
        if ( geoFenceLimits != null )
            geoFenceLimits.remove();
    }
}