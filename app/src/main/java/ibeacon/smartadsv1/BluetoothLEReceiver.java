package ibeacon.smartadsv1;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ibeacon.smartadsv1.service.BeaconService;

public class BluetoothLEReceiver extends BroadcastReceiver {

    private Intent beaconServiceIntent = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        beaconServiceIntent = new Intent(context, BeaconService.class);
        Log.d("BluetoothLE", "On Receive starting");

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_OFF: {
                    Log.d("Bluetooth state", "STATE OFF");
                    context.stopService(beaconServiceIntent);
                    }
                    break;
                case BluetoothAdapter.STATE_ON: {
                    Log.d("BluetoothLE", "Starting service");
                    context.startService(beaconServiceIntent);
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
