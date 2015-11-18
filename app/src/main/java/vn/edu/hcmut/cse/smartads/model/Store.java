package vn.edu.hcmut.cse.smartads.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by Huy on 11/18/2015.
 */
public class Store extends SugarRecord<Store> {

    @Ignore
    public static final String STORE_ID = "id";
    public static final String STORE_LAT = "latitude";
    public static final String STORE_LON = "longitude";

    private String storeId;
    private double latitude;
    private double longtitude;

    public Store() {

    }

    public Store(String storeId, double latitude, double longtitude) {
        this.storeId = storeId;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
