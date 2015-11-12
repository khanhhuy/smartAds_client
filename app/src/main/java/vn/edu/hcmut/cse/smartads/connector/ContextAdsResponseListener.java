package vn.edu.hcmut.cse.smartads.connector;

import java.util.List;

import vn.edu.hcmut.cse.smartads.listener.MyBeacon;


/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsResponseListener {
    void onReceivedContextAds(List<MyBeacon> receivedBeacons);

    void onConnectError();
}
