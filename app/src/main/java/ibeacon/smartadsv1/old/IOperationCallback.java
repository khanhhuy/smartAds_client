package ibeacon.smartadsv1.old;

import java.util.List;

import ibeacon.smartadsv1.model.Ad;

/**
 * Created by Huy on 5/10/2015.
 */
public interface IOperationCallback {
    void receivedContextAds(List<Ad> contextAdList);
}
