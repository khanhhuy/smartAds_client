package vn.edu.hcmut.cse.smartads.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.activity.SettingsActivity;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.connector.SimpleResponseListener;
import vn.edu.hcmut.cse.smartads.settings.PromotionNotifyConditionPreference;
import vn.edu.hcmut.cse.smartads.settings.RateValueGroup;
import vn.edu.hcmut.cse.smartads.settings.SettingServiceRequestType;
import vn.edu.hcmut.cse.smartads.settings.SettingsResponseListener;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

public class RemoteSettingService extends Service implements SettingsResponseListener, SimpleResponseListener {
    public static final String SERVICE_REQUEST_TYPE = "SETTINGS.SERVICE_REQUEST_TYPE";
    private String mCustomerID;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Config.TAG, "RemoteSettingService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SettingServiceRequestType requestType = (SettingServiceRequestType) intent.getSerializableExtra(SERVICE_REQUEST_TYPE);
        if (requestType == SettingServiceRequestType.UPDATE_TO_SERVER) {
            Map<String, String> settings = new HashMap<>();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            RateValueGroup group = Utils.parseStringToRateValueGroup(
                    sharedPreferences.getString(getString(R.string.settings_pref_entrance_key), null));
            if (group != null) {
                settings.put("min_entrance_rate", group.getRate().toString());
                settings.put("min_entrance_value", group.getValue().toString());
            }

            group = Utils.parseStringToRateValueGroup(
                    sharedPreferences.getString(getString(R.string.settings_pref_aisle_key), null));
            if (group != null) {
                settings.put("min_aisle_rate", group.getRate().toString());
                settings.put("min_aisle_value", group.getValue().toString());
            }

            Connector.getInstance(this).updateSettings(Utils.getCustomerID(this), settings, this);
        } else if (requestType==SettingServiceRequestType.RESTORE_FROM_SERVER) {
            Connector connector = Connector.getInstance(this);
            mCustomerID = Utils.getCustomerID(this);
            if (mCustomerID != null) {
                connector.requestSettings(mCustomerID, this);
            } else {
                stopSelf();
            }
        }
        else {
            stopSelf();
        }


        return START_REDELIVER_INTENT;
    }

    /**
     * Receive Settings from Server
     */
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
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Config.TAG, "RemoteSettingService onDestroy");
    }

    /**
     * Update To Server Success
     */
    @Override
    public void onSuccess() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingsActivity.QUEUE_SYNC, false);
        boolean everUpdated = sharedPreferences.getBoolean(SettingsActivity.EVER_UPDATED, false);
        if (!everUpdated) {
            editor.putBoolean(SettingsActivity.EVER_UPDATED, true);
        }
        editor.apply();

        stopSelf();
    }
}
