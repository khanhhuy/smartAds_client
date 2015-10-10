package vn.edu.hcmut.cse.smartads.old;

import java.util.List;

import vn.edu.hcmut.cse.smartads.model.Ads;


/**
 * Created by Huy on 5/10/2015.
 */
public interface IOperationCallback {
    void receivedContextAds(List<Ads> contextAdsList);
}
