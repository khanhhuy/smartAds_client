package vn.edu.hcmut.cse.smartads.listener;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Huy on 5/1/2015.
 */
public class BeaconFilterer {

    private static BeaconFilterer sInstance;

    private Map<String, MyBeacon> mReceivedBeacons = new HashMap<>();

    public static synchronized BeaconFilterer getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconFilterer();
        }
        return sInstance;
    }

    public List<Beacon> filterBeacons(List<Beacon> contextBeaconList) {
        List<Beacon> filteredBeacons = new ArrayList<>();
        for (Beacon beacon : contextBeaconList) {
            MyBeacon myBeacon = mReceivedBeacons.get(getKey(beacon));
            if (myBeacon != null){
                if (!myBeacon.isRefresh()){
                    continue;
                }
            }
            else {
                myBeacon = new MyBeacon(beacon);
                mReceivedBeacons.put(getKey(beacon), myBeacon);
            }
            myBeacon.refreshLastReceived();
            filteredBeacons.add(myBeacon);
        }

        return filteredBeacons;
    }

    private String getKey(Beacon beacon) {
        return beacon.getMajor() + "|" + beacon.getMinor();
    }

}
