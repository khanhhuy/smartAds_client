package ibeacon.smartadsv1.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import static com.estimote.sdk.BeaconManager.MonitoringListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ibeacon.smartadsv1.util.BundleDefined;


public class BeaconService extends Service {

    private BeaconManager beaconManager;
    private Region region;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    @Override
    public void onCreate() {
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(5), 10);
        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(5), 10);
        region = new Region("rID", null, null, null);

        beaconManager.setMonitoringListener(new MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {

                Log.d("onEnterRegion: List beacons", String.format("%d", beacons.size()));
                Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
                Bundle bundle = new Bundle();
                ArrayList<Beacon> beaconArrayList = new ArrayList<>(beacons);
                bundle.putParcelableArrayList(BundleDefined.LIST_BEACON, beaconArrayList);
                bundle.putString(BundleDefined.INTENT_TYPE, BundleDefined.INTENT_RECEIVEDBEACONS);
                intentOP.putExtras(bundle);
                Log.d("Thread onEnteredRegion", String.format("%d", android.os.Process.myTid()));

                startService(intentOP);

            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d("Region", "Exit region");
                Intent localBroadcastIntent = new Intent("regionExited");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d("Beacon", "Ranged beacons: " + beacons);
                Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
                Bundle bundle = new Bundle();
                ArrayList<Beacon> beaconArrayList = new ArrayList<>(beacons);
                bundle.putParcelableArrayList(BundleDefined.LIST_BEACON, beaconArrayList);
                bundle.putString(BundleDefined.INTENT_TYPE, BundleDefined.INTENT_RECEIVEDBEACONS);
                intentOP.putExtras(bundle);
                Log.d("Thread onEnteredRegion", String.format("%d", android.os.Process.myTid()));

                startService(intentOP);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    //Todo: Monitoring vs Ranging.
                    //beaconManager.startMonitoring(region);
                    beaconManager.startRanging(region);
                } catch (Exception e){}
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Beacon manager", "Disconnect");
        beaconManager.disconnect();

        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleDefined.INTENT_TYPE, BundleDefined.INTENT_STOPSERVICE);
        intentOP.putExtras(bundle);
        startService(intentOP);

        super.onDestroy();
    }
}
