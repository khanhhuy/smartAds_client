package vn.edu.hcmut.cse.smartads.settings;

import java.math.BigDecimal;

import vn.edu.hcmut.cse.smartads.connector.CustomResponseListener;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public interface SettingsResponseListener extends CustomResponseListener {
    void onSuccess(Integer minEntranceRate, BigDecimal minEntranceValue, Integer minAisleRate, BigDecimal minAisleValue);
}
