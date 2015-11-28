package vn.edu.hcmut.cse.smartads.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.model.Minor;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

import static com.estimote.sdk.BeaconManager.MonitoringListener;


public class ContextAdsService extends Service implements ContextAdsResponseListener {

    private static final String SMART_AS_UUID = "3FED567D-89B1-5B37-1B15-7A1E9208B457";
    public static final String UPDATE_PREFS_TIME = "updatePrefsTime";
    public static final String LAST_ASK_FOR_INTERNET = "LAST_ASK_FOR_INTERNET";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String RECEIVE_CONTEXT_ADS = "RECEIVE_CONTEXT_ADS";
    public static final long MONITORING_SCAN_PERIOD = TimeUnit.SECONDS.toMillis(5);
    public static final long MONITORING_SLEEP_PERIOD = TimeUnit.MINUTES.toMillis(1);
    public static final long RANGING_SLOW_SLEEP_PERIOD = TimeUnit.SECONDS.toMillis(9);
    public static final long RANGING_SLEEP_PERIOD = TimeUnit.SECONDS.toMillis(2);
    public static final long RANGING_SCAN_PERIOD = TimeUnit.SECONDS.toMillis(1);

    private BeaconManager beaconManager;
    private Region SMART_ADS_REGION = new Region("All_Region", null, null, null);
    private boolean mIsStarted = false;
    private boolean mInRegion = false;
    private DateTime mLastInRegion = new DateTime();
    private BeaconFilterer mFilterer;
    private Connector mConnector;
    private ScanMode mScanMode = ScanMode.MONITORING;
    private SharedPreferences mUpdateTimePref;
    final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private DateTime mlastNotifySoundTime;
    private static NotificationManager mNotificationManager;


    @Override
    public void onCreate() {
        Log.d("DHSmartAds", "ContextAdsService onCreate");
        JodaTimeAndroid.init(this);
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(MONITORING_SCAN_PERIOD, MONITORING_SLEEP_PERIOD);
//        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
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
                Log.d(Config.TAG, "onEnteredRegion " + mScanMode);
                if (mScanMode == ScanMode.MONITORING) {
                    processBeacons(beacons);
                    beaconManager.setForegroundScanPeriod(RANGING_SCAN_PERIOD, RANGING_SLEEP_PERIOD);
                    mScanMode = ScanMode.RANGING;
                    beaconManager.startRanging(region);
                }
                mInRegion = true;
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d(Config.TAG, "onExitedRegion");
                mInRegion = false;
                mLastInRegion = DateTime.now();
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                processBeacons(beacons);
                if (!mInRegion && beacons.isEmpty()) {
                    Log.d(Config.TAG, mScanMode.toString());
                    DateTime now = DateTime.now();
                    if (DateTimeComparator.getInstance().
                            compare(now.minusMinutes(Config.DELAY_RANGING_SLOW_MIN), mLastInRegion) >= 0) {
                        if (mScanMode == ScanMode.RANGING) {
                            beaconManager.stopRanging(region);
                            beaconManager.setForegroundScanPeriod(RANGING_SCAN_PERIOD, RANGING_SLOW_SLEEP_PERIOD);
                            mScanMode = ScanMode.RANGING_SLOW;
                            beaconManager.startRanging(region);
                        } else {
                            if (mScanMode == ScanMode.RANGING_SLOW && DateTimeComparator.getInstance().
                                    compare(now.minusHours(Config.DELAY_MONITORING_HOUR), mLastInRegion) > 0) {
                                mScanMode = ScanMode.MONITORING;
                                beaconManager.stopRanging(region);
                            }
                        }
                    }
                } else if (mScanMode == ScanMode.RANGING_SLOW && !beacons.isEmpty()) {
                    beaconManager.stopRanging(region);
                    beaconManager.setForegroundScanPeriod(RANGING_SCAN_PERIOD, RANGING_SLEEP_PERIOD);
                    mScanMode = ScanMode.RANGING;
                    beaconManager.startRanging(region);
                }
            }
        });
    }

    private synchronized void processBeacons(List<Beacon> beacons) {
        if (beacons.isEmpty()) {
            return;
        }

        DateTime currentTime = new DateTime();
        String lastUpdatedStr = mUpdateTimePref.getString(LAST_UPDATED, "");

//        Log.d(Config.TAG, "Last updated from server: " + lastUpdatedStr);

        if (!lastUpdatedStr.isEmpty()) {
            DateTime lastUpdated = DateTime.parse(lastUpdatedStr, formatter);
            if (DateTimeComparator.getInstance().
                    compare(currentTime.minusSeconds(Config.SERVER_UPDATE_REQUEST_MIN_SEC), lastUpdated) > 0) {
                String customerID = Utils.getCustomerID(this);
                requestContextAds(customerID, beacons);
            } else {
                beacons = mFilterer.filterBeacons(beacons);
                if (beacons.isEmpty()) {
                    return;
                }
                checkNotifyAds(beacons);
            }
        } else {
            String customerID = Utils.getCustomerID(this);
            requestContextAds(customerID, beacons);
        }
    }

    private void requestContextAds(String customerID, List<Beacon> beacons) {
        if (TextUtils.isEmpty(customerID)) {
            stopSelf();
        } else {
            Log.d(Config.TAG, "Request Ads and Update request");
            mConnector.requestContextAds(customerID, beacons, ContextAdsService.this);
            mConnector.updateRequest();
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
                        beaconManager.startMonitoring(SMART_ADS_REGION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("Beacon manager", "Disconnect");

        beaconManager.stopRanging(SMART_ADS_REGION);
        beaconManager.stopMonitoring(SMART_ADS_REGION);
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    public void onReceivedContextAds(List<Beacon> receivedBeacons) {
        checkNotifyAds(receivedBeacons);

        SharedPreferences.Editor editor = mUpdateTimePref.edit();
        editor.putString(LAST_UPDATED, formatter.print(new DateTime()));
        editor.apply();
    }

    private void checkNotifyAds(List<Beacon> receivedBeacons) {
        Iterator<Ads> allAds = Ads.findAll(Ads.class);
        List<Ads> notifyAds = new ArrayList<>();
        boolean haveNewReceiveAds = false;

        while (allAds.hasNext()) {
            Ads ads = allAds.next();
            if (ads.isNewReceivedAds()) {
                if (!haveNewReceiveAds) {
                    haveNewReceiveAds = true;
                }
                ads.markReceived();
                ads.save();
            }

            List<Integer> adsMinors = ads.getMinors();
            for (Beacon beacon : receivedBeacons) {
                if (isNotifiedAds(ads, adsMinors, beacon.getMinor())) {
                    if (!Config.DEBUG) {
                        ads.setNotified(true);
                    }
                    ads.setLastReceived(new DateTime());
                    notifyAds.add(ads);
                    break;
                }
            }
        }

        notifyAds(notifyAds);
        for (Ads ads : notifyAds) {
            ads.insertOrUpdate();
        }

        if (haveNewReceiveAds) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(RECEIVE_CONTEXT_ADS));
        }
    }

    private boolean isNotifiedAds(Ads ads, List<Integer> adsMinor, int minor) {
        if (!ads.getType().equals(Ads.ENTRANCE_PROMOTIONS) && !ads.getType().equals(Ads.TARGETED_ADS) && !adsMinor.contains(minor))
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
//            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addParentStack(ViewDetailAdsActivity.class);
//            stackBuilder.addNextIntent(notifyIntent);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(1000),
                    notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(new Random().nextInt(1000), PendingIntent.FLAG_UPDATE_CURRENT);


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
            mNotificationManager.notify(Config.TAG, ads.getAdsId(), notification);
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
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(-1, builder.build());
                mUpdateTimePref.edit().putString(LAST_ASK_FOR_INTERNET, DateTime.now().toString()).apply();
            }
        }
    }

    public static void checkBluetoothAndStart(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            context.startService(new Intent(context, ContextAdsService.class));
        }
    }
}