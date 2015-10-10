package vn.edu.hcmut.cse.smartads.listener;

import java.util.List;

import vn.edu.hcmut.cse.smartads.model.Ads;


/**
 * Created by Huy on 6/10/2015.
 */
public interface AdsContentListener {
    void adsListChange(List<Ads> newAds);
    void adsListUpdateImg(int position);
}
