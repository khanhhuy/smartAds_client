package vn.edu.hcmut.cse.smartads.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.activity.ViewDetailAdsActivity;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.connector.ContextAdsResponseListener;
import vn.edu.hcmut.cse.smartads.listener.BeaconFilterer;
import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.model.Minor;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

import static com.estimote.sdk.BeaconManager.MonitoringListener;


public class ContextAdsService extends Service implements ContextAdsResponseListener {

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    public static final String UPDATE_PREFS_TIME = "updatePrefsTime";
    public static final String LAST_ASK_FOR_INTERNET = "LAST_ASK_FOR_INTERNET";
    public static final String LAST_UPDATED = "lastUpdated";

    private BeaconManager beaconManager;
    private Region region;
    private boolean mIsStarted = false;
    private BeaconFilterer mFilterer;
    private Connector mConnector;
    private SharedPreferences mUpdateTimePref;
    final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTime mlastNotifySoundTime;
    private static NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        Log.d("DHSmartAds", "ContextAdsService onCreate");
        JodaTimeAndroid.init(this);
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(10));
        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(0));
        region = new Region("All_Region", null, null, null);
        mFilterer = BeaconFilterer.getInstance();
        mConnector = Connector.getInstance(this);
        mUpdateTimePref = getSharedPreferences(UPDATE_PREFS_TIME, MODE_PRIVATE);

        if (Config.DEBUG) {
            Ads.deleteAll(Ads.class);
            Minor.deleteAll(Minor.class);
            //Todo: testing here
        }

        beaconManager.setMonitoringListener(new MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                Log.d(Config.TAG, "onEnteredRegion");
                try {
                    SharedPreferences.Editor editor = mUpdateTimePref.edit();
                    editor.putString(LAST_UPDATED, formatter.print(new DateTime()));
                    editor.apply();
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
                processBeacons(beacons);
            }
        });
    }

    private void processBeacons(List<Beacon> beacons) {

        List<MyBeacon> filteredBeacons = mFilterer.filterBeacons(beacons);

        if (filteredBeacons.isEmpty())
            return;

        DateTime current_time = new DateTime();
        String last_updatedStr = mUpdateTimePref.getString(LAST_UPDATED, "");

        Log.d(Config.TAG, "Last updated from server: " + last_updatedStr);

        if (!last_updatedStr.isEmpty()) {
            DateTime last_updated = DateTime.parse(last_updatedStr, formatter);
            if (DateTimeComparator.getInstance().
                    compare(current_time.minusDays(Config.SERVER_UPDATE_REQUEST_MIN_DATE), last_updated) > 0) {
                Log.d(Config.TAG, "Request Ads and Update request");
                String customerID = Utils.getCustomerID(this);
                if (TextUtils.isEmpty(customerID)) {
                    stopSelf();
                } else {
                    mConnector.requestContextAds(customerID, filteredBeacons, ContextAdsService.this);
                    mConnector.updateRequest();
                }
            }
        } else {
            onReceivedContextAds(filteredBeacons);
        }
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
            Log.e(Config.TAG, "Cannot stop but it does not matter now", e);
        }

        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    public void onReceivedContextAds(List<MyBeacon> receivedBeacons) {

        Iterator<Ads> allAds = Ads.findAll(Ads.class);
        List<Ads> notifyAds = new ArrayList<>();

        while (allAds.hasNext()) {
            Ads ads = allAds.next();
            List<Integer> adsMinors = ads.getMinors();
            for (MyBeacon beacon : receivedBeacons)
                if (isNotifiedAds(ads, adsMinors, beacon.getMinor())) {
                    if (!Config.DEBUG) {
                        ads.setNotified(true);
                    }
                    ads.setLastReceived(new DateTime());
                    notifyAds.add(ads);
                    break;
                }
        }

        notifyAds(notifyAds);
        for (Ads ads : notifyAds) {
            ads.InsertOrUpdate();
        }

    }

    private boolean isNotifiedAds(Ads ads, List<Integer> adsMinor, int minor) {
        if (!ads.getType().equals(Ads.ENTRANCE_ADS) && !adsMinor.contains(minor))
            return false;
        if (ads.is_notified() || ads.is_blacklisted())
            return false;
        DateTime currentDate = new DateTime();
        if ((ads.getStartDate() != null) && (ads.getStartDate().compareTo(currentDate) > 0))
            return false;
        if ((ads.getEndDate() != null) && (ads.getEndDate().compareTo(currentDate) < 0))
            return false;

        return true;
    }

    public void notifyAds(List<Ads> notifyAdsList) {

        if (notifyAdsList.isEmpty()) {
            return;
        }

        Bundle bundle;


        for (Ads ads : notifyAdsList) {

            bundle = new Bundle();
            String urlPath = Config.HOST_BASE + "/ads/" + String.valueOf(ads.getAdsId());
            bundle.putString(BundleDefined.URL, urlPath);
            bundle.putString(BundleDefined.ADS_ID, String.valueOf(ads.getAdsId()));

            Intent notifyIntent = new Intent(this, ViewDetailAdsActivity.class);
            notifyIntent.putExtras(bundle);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(1000),
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(Config.APP_NAME)
                    .setContentText(ads.getTitle())
                    .setDefaults(Notification.FLAG_AUTO_CANCEL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);


            if ((mlastNotifySoundTime == null) ||
                    (mlastNotifySoundTime.compareTo(
                            (new DateTime()).minusSeconds(Config.MIN_NOTIFICATION_SOUND_DELAYED_SEC)) < 0)) {
                builder.setDefaults(Notification.DEFAULT_SOUND);
                mlastNotifySoundTime = new DateTime();
            }

            Notification notification = builder.build();
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.notify(Config.TAG, new Random().nextInt(1000) + 1, notification);
        }
    }

    public static NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    @Override
    public void onConnectError() {
        if (!Utils.isNetworkConnected(this)) {
            String lastAskedString = mUpdateTimePref.getString(LAST_ASK_FOR_INTERNET, "");
            boolean ask = false;
            if (TextUtils.isEmpty(lastAskedString)) {
                ask = true;
            } else {
                DateTime lastAsked = DateTime.parse(lastAskedString);
                if (lastAsked.plusHours(12).isBeforeNow() || (Config.DEBUG && lastAsked.plusMinutes(1).isBeforeNow())) {
                    ask = true;
                }
            }

            if (ask) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(getString(R.string.ask_for_internet_title)).setContentText(getString(R.string.ask_for_internet_text)).setAutoCancel(true).
                        setDefaults(Notification.DEFAULT_ALL);
//                Intent i = new Intent(Settings.ACTION_SETTINGS);
//                PendingIntent settings = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(settings);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(-1, builder.build());
                mUpdateTimePref.edit().putString(LAST_ASK_FOR_INTERNET, DateTime.now().toString()).apply();
            }
        }
    }
}