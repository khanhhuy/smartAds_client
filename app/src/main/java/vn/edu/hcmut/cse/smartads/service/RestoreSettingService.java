package vn.edu.hcmut.cse.smartads.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.BigDecimal;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.settings.PromotionNotifyConditionPreference;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

public class RestoreSettingService extends Service implements SettingsResponseListener {
    public static final int MAX_RETRY = 2;
    private int mRetry = 0;
    private String mCustomerID;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Config.TAG,"RestoreSettingService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Connector connector = Connector.getInstance(this);
        mCustomerID = Utils.getCustomerID(this);
        if (mCustomerID != null) {
            connector.requestSettings(mCustomerID, this);
        } else {
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onSuccess(Integer minEntranceRate, BigDecimal minEntranceValue, Integer minAisleRate, BigDecimal minAisleValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean put = false;
        if (minEntranceRate != null && minEntranceValue != null) {
            String entranceSetting = minEntranceRate + PromotionNotifyConditionPreference.DELIMITER + minEntranceValue.toString();
            editor.putString(getString(R.string.settings_pref_entrance_key), entranceSetting);
            put = true;
        }

        if (minAisleRate != null && minAisleValue != null) {
            String aisleSetting = minAisleRate + PromotionNotifyConditionPreference.DELIMITER + minAisleValue.toString();
            editor.putString(getString(R.string.settings_pref_aisle_key), aisleSetting);
            put = true;
        }
        if (put) {
            editor.apply();
        }
        stopSelf();
    }

    @Override
    public void onError(String message) {
        if (mRetry >= MAX_RETRY) {
            stopSelf();
        } else {
            mRetry++;
            if (mCustomerID != null) {
                Connector.getInstance(this).requestSettings(mCustomerID, this);
            } else {
                stopSelf();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Config.TAG, "RestoreSettingService onDestroy");
    }
}
