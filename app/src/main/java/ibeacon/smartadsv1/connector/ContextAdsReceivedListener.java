package ibeacon.smartadsv1.connector;

import java.util.List;

import ibeacon.smartadsv1.model.Ad;

/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsReceivedListener {
   void onReceivedContextAds(List<Ad> contextAds);
}
