package vn.edu.hcmut.cse.smartads.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import static com.estimote.sdk.BeaconManager.MonitoringListener;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.activity.ViewDetailAdsActivity;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.connector.ContextAdsReceivedListener;
import vn.edu.hcmut.cse.smartads.listener.BeaconFilterer;
import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.model.Ad;
import vn.edu.hcmut.cse.smartads.old.OperationService;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;


public class ContextAdsService extends Service implements ContextAdsReceivedListener {

    private BeaconManager beaconManager;
    private Region region;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    public static final String TAG = "DHSmartAds";
    private boolean mIsStarted = false;
    private BeaconFilterer mFilterer;
    private Connector mConnector;
    static private Handler uiHandler;

    @Override
    public void onCreate() {
        Log.d("DHSmartAds", "ContextAdsService onCreate");
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        //beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(5), 1);
        region = new Region("rID", null, null, null);
        mFilterer = BeaconFilterer.getInstance();
        mConnector = Connector.getInstance(this);
        beaconManager.setMonitoringListener(new MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                Log.d("DHSmartAds", "onEnteredRegion");
                processBeacons(beacons);
                try {
                    beaconManager.startRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d("DHSmartAds", "onExitedRegion");
                try {
                    beaconManager.stopRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d("DHSmartAds", "onBeaconsDiscovered " + beacons.size());
                processBeacons(beacons);
            }
        });
    }

    private void processBeacons(List<Beacon> beacons) {
        List<MyBeacon> filteredBeacons = mFilterer.filterBeacons(beacons);
        mConnector.requestContextAds(filteredBeacons, ContextAdsService.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mIsStarted) {
            mIsStarted = true;
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        beaconManager.startMonitoring(region);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Beacon manager", "Disconnect");

        try {
            beaconManager.stopMonitoring(region);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        beaconManager.disconnect();

        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleDefined.INTENT_TYPE, BundleDefined.INTENT_STOPSERVICE);
        intentOP.putExtras(bundle);
        startService(intentOP);


        super.onDestroy();
    }

    @Override
    public void onReceivedContextAds(List<Ad> contextAdList) {

        //Todo: Check if these ads should be displayed to user ?

        if (contextAdList.isEmpty()) {
            return;
        }

        Bundle bundle;

        for (Ad contextAd : contextAdList){

            bundle = new Bundle();
            String urlPath = Config.HOST + "/ads/" + String.format("%d", contextAd.getId());
            bundle.putString(BundleDefined.URL, urlPath);

            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Received new Ad")
                    .setContentText(contextAd.getTitle())
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL);


            Intent notifyIntent = new Intent(this, ViewDetailAdsActivity.class);
            notifyIntent.putExtras(bundle);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(1000) + 1,
                                                                        notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notiBuilder.setContentIntent(pendingIntent).setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(1000) + 1, notiBuilder.build());

        }



//        new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message inputMessage) {
//
//                NotificationCompat.Builder notiBuilder = (NotificationCompat.Builder) inputMessage.obj;
//                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                notificationManager.notify(1, notiBuilder.build());
//            }
//        }.obtainMessage(MessageDefined.START_ACTIVITY, notiBuilder).sendToTarget();
//
//        uiHandler.obtainMessage(MessageDefined.START_ACTIVITY, notiBuilder).sendToTarget();

    }

}
