package vn.edu.hcmut.cse.smartads.activity;

import android.app.NotificationManager;

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

}
