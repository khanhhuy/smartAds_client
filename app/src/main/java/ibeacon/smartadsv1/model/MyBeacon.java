package ibeacon.smartadsv1.model;

import android.util.Log;

import com.estimote.sdk.Beacon;

import ibeacon.smartadsv1.util.Config;

/**
 * Created by Huy on 5/1/2015.
 */
public class MyBeacon extends Beacon {

    private Long lastReceived;

    public MyBeacon(Beacon beacon) {
        super(beacon.getProximityUUID(), beacon.getName(), beacon.getMacAddress(), beacon.getMajor(), beacon.getMinor(),
                beacon.getMeasuredPower(), beacon.getRssi());

    }

    public MyBeacon(String proximityUUID, String name, String macAddress, int major, int minor, int measuredPower, int rssi) {

        super(proximityUUID, name, macAddress, major, minor, measuredPower, rssi);

    }

    public Long getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(Long lastReceived) {
        this.lastReceived = lastReceived;
    }

    public boolean isRefresh() {
        Log.d("Refresh time (ms)", String.format("%d", System.currentTimeMillis() - lastReceived));
        if ((System.currentTimeMillis() - lastReceived) > Config.MIN_RECEIVED_TIME_SEC*1000)
            return  true;
        else
            return false;
    }

//    @Override
//    public boolean equals(Object v) {
//        boolean rEqual = false;
//
//        if (v instanceof Beacon) {
//            Beacon ptr = (Beacon) v;
//            if (ptr.getProximityUUID() == this.getProximityUUID()
//                && ptr.getMajor() == this.getMajor() && ptr.getMinor() == this.getMinor())
//            rEqual = true;
//        }
//
//        return rEqual;
//    }
}
