package vn.edu.hcmut.cse.smartads.listener;

import android.os.Parcel;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.MacAddress;

import java.util.UUID;

import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Huy on 5/1/2015.
 */
class MyBeacon extends Beacon {

    private Long lastReceived;

    public static final Creator<MyBeacon> CREATOR = new Creator<MyBeacon>() {
        public MyBeacon createFromParcel(Parcel source) {
            UUID proximityUUID = (UUID) source.readValue(UUID.class.getClassLoader());
            MacAddress macAddress = (MacAddress) source.readValue(MacAddress.class.getClassLoader());
            int major = source.readInt();
            int minor = source.readInt();
            int measuredPower = source.readInt();
            int rssi = source.readInt();
            return new MyBeacon(proximityUUID, macAddress, major, minor, measuredPower, rssi);
        }

        public MyBeacon[] newArray(int size) {
            return new MyBeacon[size];
        }
    };

    public MyBeacon(Beacon beacon) {
        super(beacon.getProximityUUID(), beacon.getMacAddress(), beacon.getMajor(), beacon.getMinor(),
                beacon.getMeasuredPower(), beacon.getRssi());
    }

    public MyBeacon(UUID proximityUUID, MacAddress macAddress, int major, int minor, int measuredPower, int rssi) {
        super(proximityUUID, macAddress, major, minor, measuredPower, rssi);
    }

    public Long getLastReceived() {
        return lastReceived;
    }

    public boolean isRefresh() {
        Log.d(Config.TAG, "Minor " + getMinor() + " Refresh time " + (System.currentTimeMillis() - lastReceived));
        return (System.currentTimeMillis() - lastReceived) > Config.BEACON_MIN_RECEIVED_TIME_SEC * 1000;
    }

    public void refreshLastReceived() {
        lastReceived=System.currentTimeMillis();
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
