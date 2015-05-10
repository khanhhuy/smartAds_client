package ibeacon.smartadsv1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.estimote.sdk.Beacon;

/**
 * Created by Huy on 5/1/2015.
 */
public class MyBeaconManager {

    private HashMap<String, MyBeacon> listReceivedBeacon;

    public MyBeaconManager() {
        listReceivedBeacon = new HashMap<>();
    }

    public boolean isNewBeacon(Beacon newBeacon) {

        MyBeacon beacon = listReceivedBeacon.get(newBeacon.getProximityUUID());
        if (beacon.getMajor() == newBeacon.getMajor()
                && beacon.getMinor() == newBeacon.getMinor())
        {
            if (beacon.isRefresh())
                return true;
            else
                return false;
        }
        else {
            listReceivedBeacon.put(newBeacon.getProximityUUID(), new MyBeacon(newBeacon));
            return true;
        }
    }
}
