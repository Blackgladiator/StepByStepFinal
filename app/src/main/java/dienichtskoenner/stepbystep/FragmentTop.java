package dienichtskoenner.stepbystep;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.Fitness.SensorsApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTop extends AppCompatActivity {

    public static final String TAG = "BasicSensorApi";
    private GoogleApiClient mClient = null;
    private static final int  REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private OnDataPointListener mlistener;



    public FragmentTop() {
        // Required empty public constructor




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top, container, false);

        //android-fit sample by PaulRashidi
        if(!checkPermissions()) {
            requestPermissions();
        }

    }
    @Override
    protected void onResume(){
        super.onResume();
        buildFitnessClient();
    }


    private void buildFitnessClient() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SensorsApi)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    findFitnessDataSources();

                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connections lost.  Cause: Network Lost.");
                                    } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG, "Connection lost. Reason: Service Disconnected");
                                    }
                                }
                            }

                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed.  Cause:" + result.toString());
                            Snackbar.make(
                                    FragmentTop.this.findViewById(R.id.steps_solid), "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(), Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
             }

        }

        private void findFitnessDataSources(){
            SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder().setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
            .setDataSourceTypes(DataSource.TYPE_RAW)
            .build())
                    .setResultCallback(new ResultCallback<DataSourcesResult>() {
                        @Override
                        public void onResult(DataSourcesResult dataSourcesResult) {
                            Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                            for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                                Log.i(TAG, "Data source found: " + dataSource.toString());
                                Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                                if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE) && mlistener == null) {
                                    Log.i(TAG, "Datasource for LOCATION_SAMPLE found! Registering.");
                                    registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                                }
                            }
                        }
                    });

        }

        private void registerFitnessDataListener(DataSource dataSource, DataType dataType){

            mlistener = new OnDataPointListener() {
                @Override
                public void onDataPoint(DataPoint dataPoint) {
                    for(Field field : dataPoint.getDataType().getFields()){
                        Value val = dataPoint.getValue(field);
                        Log.i(TAG, "Detected DataPoint field: " + field.getName());
                        Log.i(TAG , "Detected DataPoint value: " + val);
                    }
                }
            };

            SensorsApi.add(
                    mClient, new SensorRequest.Builder()
                    .setDataSource(dataSource)
                    .setDataType(dataType)
                    .setSamplingRate(10, TimeUnit.SECONDS)
                    .build(),
                    mlistener)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if(status.isSuccess()){
                                Log.i(TAG, "Listener registered!");
                            }else{
                                Log.i(TAG, "Listener not registered.");
                            }
                        }
                    });

        }

        private void unregisterFitnessDataListener(){
            if(mlistener == null) {
                return;
            }

            SensorsApi.remove(
                    mClient,
                    mlistener)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if(status.isSuccess()) {
                                Log.i(TAG, "Listener was removed!");
                            }else{
                                Log.i(TAG, "Listener was not removed.");
                            }
                        }
                    });


        }



        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.intent_action){
                unregisterFitnessDataListener();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    private  boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if(shouldProvideRationale) {
                Log.i(TAG, "Requesting permission");
                ActivityCompat.requestPermissions(FragmentTop.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                buildFitnessClient();
            } else {

                Snackbar.make(
                        findViewById(R.id.main_activity_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.Data(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }


    }


