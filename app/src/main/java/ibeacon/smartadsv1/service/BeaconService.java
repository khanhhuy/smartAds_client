package ibeacon.smartadsv1.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import static com.estimote.sdk.BeaconManager.MonitoringListener;

import java.util.List;
import java.util.concurrent.TimeUnit;



public class BeaconService extends Service {

    private BeaconManager beaconManager;
    private Region region;

    @Override
    public void onCreate() {
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        region = new Region("regionID001", null, null, null);

        beaconManager.setMonitoringListener(new MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    Log.d("Beacon received", beacon.getProximityUUID());

                    Intent intentOP = new Intent(getApplicationContext(), OperationService.class);

                    //Todo: Make a parcelable class for beacon

                    intentOP.putExtra("uuid", beacon.getProximityUUID());
                    startService(intentOP);


//                  String threadID = new Integer(android.os.Process.myTid()).toString();
//                  Log.d("Thread onEnteredRegion", threadID);
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d("Region", "Exit region");
                Intent localBroadcastIntent = new Intent("regionExited");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);
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
                    beaconManager.startMonitoring(region);
                } catch (Exception e){}
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Beacon manager", "Disconnect");
        beaconManager.disconnect();

        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        stopService(intentOP);

        super.onDestroy();
    }
}
