package vn.edu.hcmut.cse.smartads.connector;

import com.estimote.sdk.Beacon;

import java.util.List;


/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsResponseListener {
    void onReceivedContextAds(List<Beacon> receivedBeacons);

    void onConnectError();
}
