package vn.edu.hcmut.cse.smartads.settings;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

import java.math.BigDecimal;

import vn.edu.hcmut.cse.smartads.R;

/**
 * Created by Minh Dao Bui on 10/18/2015.
 */
public class SettingsValidator {

    public static boolean validateNotifyCondition(PromotionNotifyConditionPreference preference) {
        Context context = preference.getContext();
        PromotionNotifyConditionPreference entrancePref, aislePref;
        if (context.getString(R.string.settings_pref_entrance_key).equals(preference.getKey())) {
            entrancePref = preference;
            aislePref = (PromotionNotifyConditionPreference) preference.getPreferenceManager().findPreference(context.getString(R.string.settings_pref_aisle_key));
        } else {
            aislePref = preference;
            entrancePref = (PromotionNotifyConditionPreference) preference.getPreferenceManager().findPreference(context.getString(R.string.settings_pref_entrance_key));
        }
        return validateNotifyCondition(entrancePref.getDiscountRate(), entrancePref.getDiscountValue(),
                aislePref.getDiscountRate(), aislePref.getDiscountValue());
    }

    private static boolean validateNotifyCondition(int entranceRate, BigDecimal entranceValue, int aisleRate, BigDecimal aisleValue) {
        return !(entranceRate < aisleRate || entranceValue.compareTo(aisleValue) < 0);
    }
}
