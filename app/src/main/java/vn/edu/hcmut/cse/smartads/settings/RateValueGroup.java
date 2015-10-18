package vn.edu.hcmut.cse.smartads.settings;

import java.math.BigDecimal;

/**
 * Created by Minh Dao Bui on 10/18/2015.
 */
public class RateValueGroup {
    private final Integer mRate;
    private final BigDecimal mValue;


    public RateValueGroup(Integer mRate, BigDecimal mValue) {
        this.mRate = mRate;
        this.mValue = mValue;
    }

    public Integer getRate() {
        return mRate;
    }

    public BigDecimal getValue() {
        return mValue;
    }

}
