package vn.edu.hcmut.cse.smartads.connector;

import java.util.List;

import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.model.Ads;


/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsReceivedListener {
    void onReceivedContextAds(List<MyBeacon> receivedBeacons);
}
