package ibeacon.smartadsv1.listener;

import java.util.List;

import ibeacon.smartadsv1.model.Ad;

/**
 * Created by Huy on 6/10/2015.
 */
public interface AdsContentListener {
    void adsListChange(List<Ad> newAds);
    void adsListUpdateImg(int position);
}
