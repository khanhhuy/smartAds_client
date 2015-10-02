package vn.edu.hcmut.cse.smartads.listener;

import java.util.List;

import vn.edu.hcmut.cse.smartads.model.Ad;


/**
 * Created by minhdaobui on 6/3/2015.
 */
public interface ContextAdsReceivedListener {
   void onReceivedContextAds(List<Ad> contextAds);
}
