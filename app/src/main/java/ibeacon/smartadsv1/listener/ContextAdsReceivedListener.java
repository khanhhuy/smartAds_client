package ibeacon.smartadsv1.listener;

import java.util.List;

import ibeacon.smartadsv1.model.Ad;

/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsReceivedListener {
   void onReceivedContextAds(List<Ad> contextAds);
}
