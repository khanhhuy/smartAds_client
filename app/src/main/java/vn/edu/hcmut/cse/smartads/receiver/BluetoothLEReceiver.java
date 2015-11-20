package vn.edu.hcmut.cse.smartads.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.GeofenceManager;
import vn.edu.hcmut.cse.smartads.util.Utils;


public class BluetoothLEReceiver extends BroadcastReceiver {

    private Intent beaconServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Config.TAG, "Bluetooth trigger");
        if (!Utils.isCustomerLoggedIn(context)) {
            return;
        }
        final String action = intent.getAction();

        beaconServiceIntent = new Intent(context, ContextAdsService.class);

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d("Bluetooth state", "STATE OFF");
                    GeofenceManager.getInstance(context).startGeofencing();
                    Log.d(Config.TAG, "Start Geofence when Bluetooth off");
                    context.stopService(beaconServiceIntent);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d("BluetoothLE", "Starting service");
                    GeofenceManager.getInstance(context).stopGeofencing();
                    Log.d(Config.TAG, "Stop Geofence when Bluetooth on");
                    context.startService(beaconServiceIntent);
                    break;
                default:
                    break;
            }
        }

    }
}
