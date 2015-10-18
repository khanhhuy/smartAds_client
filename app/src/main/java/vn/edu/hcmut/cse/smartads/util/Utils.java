package vn.edu.hcmut.cse.smartads.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.activity.LoginActivity;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.service.RestoreSettingService;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public class Utils {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.btn_ok), null);
        builder.create().show();
    }

    public static void showAlertDialog(Context context, String message) {
        showAlertDialog(context, null, message);
    }

    public static String currencyFormat(BigDecimal value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("VI", "VN"));
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,###.## VND", symbols);
        return format.format(value);
    }

    public static String getCustomerID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LoginActivity.CUSTOMER_ID, null);
    }
}
