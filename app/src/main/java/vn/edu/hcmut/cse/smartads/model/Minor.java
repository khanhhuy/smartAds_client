package vn.edu.hcmut.cse.smartads.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Huy on 10/8/2015.
 */
public class Minor extends SugarRecord<Minor> {

    private int minor;
    private Ads ads;

    public Minor() {

    }

    public Minor(int id) {
        minor = id;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public Ads getAds() {
        return ads;
    }

    public void setAds(Ads ads) {
        this.ads = ads;
    }
}
