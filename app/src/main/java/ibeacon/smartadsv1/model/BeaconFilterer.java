package ibeacon.smartadsv1.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.estimote.sdk.Beacon;

/**
 * Created by Huy on 5/1/2015.
 */
public class BeaconFilterer {

    private static BeaconFilterer sInstance;

    private Map<Integer, MyBeacon> mReceivedBeacons = new HashMap<Integer, MyBeacon>();

    public static synchronized BeaconFilterer getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconFilterer();
        }
        return sInstance;
    }

    public List<MyBeacon> filterBeacons(List<Beacon> contextBeaconList) {
        List<MyBeacon> filteredBeacons = new ArrayList<>();
        for (Beacon beacon : contextBeaconList) {
            MyBeacon myBeacon = mReceivedBeacons.get(beacon.getMinor());
            if (myBeacon!=null){
                if (!myBeacon.isRefresh()){
                    continue;
                }
            }
            else {
                myBeacon = new MyBeacon(beacon);
                mReceivedBeacons.put(beacon.getMinor(), myBeacon);
            }
            myBeacon.refreshLastReceived();
            filteredBeacons.add(myBeacon);
        }

        return filteredBeacons;
    }

}
