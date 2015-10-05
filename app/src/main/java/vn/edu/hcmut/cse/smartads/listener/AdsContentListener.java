package vn.edu.hcmut.cse.smartads.listener;

import java.util.List;

import vn.edu.hcmut.cse.smartads.model.Ad;


/**
 * Created by Huy on 6/10/2015.
 */
public interface AdsContentListener {
    void adsListChange(List<Ad> newAds);
    void adsListUpdateImg(int position);
}
