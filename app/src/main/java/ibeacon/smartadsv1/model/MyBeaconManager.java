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
public class MyBeaconManager {

    private Map<Integer, MyBeacon> listReceivedBeacon;

    public MyBeaconManager() {
        listReceivedBeacon = new HashMap<>();
    }

    public MyBeacon addNewBeacon(Beacon newBeacon) {

        MyBeacon beacon = listReceivedBeacon.get(newBeacon.getMinor());
        if (beacon != null)
        {
            if (beacon.isRefresh())
                return beacon;
            else
                return null;
        }
        else {
            MyBeacon myBeacon = new MyBeacon(newBeacon);
            myBeacon.setLastReceived(System.currentTimeMillis());
            listReceivedBeacon.put(newBeacon.getMinor(), myBeacon);
            return myBeacon;
        }
    }

    public List<MyBeacon> getRefreshedBeacon(List<Beacon> contextBeaconList) {

        List<MyBeacon> refreshedBeaconList = new ArrayList<>();
        for (Beacon beacon : contextBeaconList) {
            MyBeacon refreshedBeacon = addNewBeacon(beacon);
            if (refreshedBeacon != null)
                refreshedBeaconList.add(refreshedBeacon);
        }

        return refreshedBeaconList;
    }

    public List<MyBeacon> getRefreshedBeacon() {

        List<MyBeacon> refreshedBeaconList = new ArrayList<>();

        Set<Map.Entry<Integer, MyBeacon>> allBeacons = listReceivedBeacon.entrySet();
        Iterator<Map.Entry<Integer, MyBeacon>> iterator = allBeacons.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().isRefresh())
                refreshedBeaconList.add(iterator.next().getValue());
        }

        return refreshedBeaconList;
    }
}
