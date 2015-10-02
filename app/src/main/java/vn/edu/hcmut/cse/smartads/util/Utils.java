package vn.edu.hcmut.cse.smartads.util;

import android.text.TextUtils;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public class Utils {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
