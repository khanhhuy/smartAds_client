package vn.edu.hcmut.cse.smartads.util;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;

import vn.edu.hcmut.cse.smartads.R;

/**
 * Created by minhdaobui on 10/1/2015.
 */
public class Utils {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void showAlertDialog(Context context,String title,String message ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.btn_ok), null);
        builder.create().show();
    }
    public static void showAlertDialog(Context context,String message ) {
        showAlertDialog(context,null,message);
    }
}
