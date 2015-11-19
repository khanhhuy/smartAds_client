package vn.edu.hcmut.cse.smartads.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Iterator;

import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.listener.LocationUpdateListener;
import vn.edu.hcmut.cse.smartads.model.Store;
import vn.edu.hcmut.cse.smartads.service.GeofenceTransitionsIntentService;

/**
 * Created by Huy on 11/17/2015.
 */
public class GeofenceManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, LocationUpdateListener {

    protected GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private final Context mContext;
    private static GeofenceManager mGeofenceInstance;
    private boolean connectToRemove = false;

    public GeofenceManager(Context context) {
        mContext = context;
    }

    public static synchronized GeofenceManager getInstance(Context context) {
        if (mGeofenceInstance == null) {
            mGeofenceInstance = new GeofenceManager(context);
        }
        return mGeofenceInstance;
    }

    public void onUpdateSuccess() {
        startGeofencing();
    }

    public void startGeofencing() {
        if (populateGeoFenceList()) {
            connectToRemove = false;
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                addGeofencesHandler();
            else {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }
        }
    }

    public void stopGeofencing() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            removeGeofencesHandler();
        }
        else {
            connectToRemove = true;
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(Config.TAG, "Connected to GoogleApiClient");
        if (mGoogleApiClient.isConnected()) {
            if (!connectToRemove)
                addGeofencesHandler();
            else {
                removeGeofencesHandler();
            }
        }
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
            Log.d(Config.TAG, "Status succeed + " + status.getStatusCode());
        } else {
            Log.d(Config.TAG, "Status failed + " + status.getStatusCode());
        }
        mGoogleApiClient.disconnect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private boolean populateGeoFenceList() {
        mGeofenceList = new ArrayList<>();
        Iterator<Store> allStores = Store.findAll(Store.class);

        if (!allStores.hasNext()) {
            Connector.getInstance(mContext).getStoreLocation(this);
            return false;
        }

        while (allStores.hasNext()) {

            Store store = allStores.next();

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(store.getStoreId())
                    .setCircularRegion(
                            store.getLatitude(),
                            store.getLongtitude(),
                            Config.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setLoiteringDelay(Config.GEOFENCE_LOITERING_IN_MINUTES)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build());

            Log.d(Config.TAG, "Geofence id: " + store.getStoreId()
                    + ", " + String.valueOf(store.getLatitude())
                    + ", " + String.valueOf(store.getLongtitude()));
        }

        return true;
    }



    private void addGeofencesHandler() {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    private void removeGeofencesHandler() {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
