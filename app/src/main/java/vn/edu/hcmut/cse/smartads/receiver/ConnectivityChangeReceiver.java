package vn.edu.hcmut.cse.smartads.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vn.edu.hcmut.cse.smartads.service.RemoteSettingService;
import vn.edu.hcmut.cse.smartads.settings.SettingServiceRequestType;
import vn.edu.hcmut.cse.smartads.util.Utils;

/**
 * Created by Minh Dao Bui on 10/18/2015.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.needToSyncSettings(context)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                Intent settingServiceIntent = new Intent(context, RemoteSettingService.class);
                settingServiceIntent.putExtra(RemoteSettingService.SERVICE_REQUEST_TYPE, SettingServiceRequestType.UPDATE_TO_SERVER);
                context.startService(settingServiceIntent);
            }
        }
    }
}
