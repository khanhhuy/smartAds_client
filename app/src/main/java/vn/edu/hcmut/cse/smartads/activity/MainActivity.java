package vn.edu.hcmut.cse.smartads.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.service.GeofenceTransitionsIntentService;
import vn.edu.hcmut.cse.smartads.settings.dev.DevConfigActivity;
import vn.edu.hcmut.cse.smartads.util.Config;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    public static final int VIEW_DETAILS_ADS = 5;
    public static final int RESULT_DELETED = 1;
    public static final int RESULT_VIEWED = 2;

    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_main);

        setTitle("Featured Ads");

        checkLoggedIn();
        initFragment();

        mGeofencePendingIntent = null;

        buildGoogleApiClient();
        populateGeoFenceList();


    }

    private void checkLoggedIn() {
        SharedPreferences authPrefs = getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, MODE_PRIVATE);
        boolean loggedIn = authPrefs.getBoolean("loggedIn", false);
        if (!loggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(this).setTitle(R.string.log_out_confirm_title).setMessage(R.string.logout_message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.logout(MainActivity.this);

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton(R.string.cancel, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Config.TAG, "MainActivity On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Config.TAG, "MainActivity On Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AdsListFragment adsListFragment = new AdsListFragment();
        adsListFragment.setup(mToolbar);

        fragmentTransaction.add(R.id.inner_container, adsListFragment);

        setTitle("Featured Ads");
        fragmentTransaction.commit();

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, DevConfigActivity.class);
            startActivity(i);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    //
    //Google API location
    //

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeGeofencesHandler();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(Config.TAG, "Connected to GoogleApiClient");
        addGeofencesHandler();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(Config.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(Config.TAG, "Connection suspended");
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this,"Status succeeded",Toast.LENGTH_SHORT).show();
        } else {
            Log.d(Config.TAG, "Status failed");
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void populateGeoFenceList() {
        mGeofenceList = new ArrayList<>();
        for (Map.Entry<String, LatLng> entry : Config.AREA_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Config.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build());
        }
    }



    public void addGeofencesHandler() {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    public void removeGeofencesHandler() {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



}
