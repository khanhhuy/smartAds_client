package vn.edu.hcmut.cse.smartads.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;


public class BluetoothLEReceiver extends BroadcastReceiver {

    private Intent beaconServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Utils.isCustomerLoggedIn(context)) {
            return;
        }
        final String action = intent.getAction();

        beaconServiceIntent = new Intent(context, ContextAdsService.class);
        Log.d(Config.TAG, "Bluetooth trigger");

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d("Bluetooth state", "STATE OFF");
                    context.stopService(beaconServiceIntent);

                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d("BluetoothLE", "Starting service");
                    context.startService(beaconServiceIntent);
                    break;
                default:
                    break;
            }
        }

    }
}
