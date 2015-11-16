package vn.edu.hcmut.cse.smartads.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.support.multidex.MultiDex;

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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
