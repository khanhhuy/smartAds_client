package vn.edu.hcmut.cse.smartads.activity;

import android.app.NotificationManager;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Huy on 10/12/2015.
 */
public class MyApplication extends com.orm.SugarApp {

    private static NotificationManager mNotificationManager;

    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public void setNotificationManager(NotificationManager manager) {
        mNotificationManager = manager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
