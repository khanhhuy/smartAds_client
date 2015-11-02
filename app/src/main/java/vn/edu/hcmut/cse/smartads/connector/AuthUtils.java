package vn.edu.hcmut.cse.smartads.connector;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import vn.edu.hcmut.cse.smartads.activity.LoginActivity;

/**
 * Created by Minh Dao Bui on 11/2/2015.
 */
public class AuthUtils {
    public static final String TOKEN_PARAM_NAME = "access_token";
    private Context mContext;

    public AuthUtils(Context context) {
        mContext = context;
    }

    public String addToken(String url) {
        String token = getToken();
        if (!token.isEmpty()) {
            if (url.contains("?")) {
                url += "&" + TOKEN_PARAM_NAME + "=" + token;
            } else {
                url += "?" + TOKEN_PARAM_NAME + "=" + token;
            }
        }
        return url;
    }

    public void addToken(Map<String, String> params) {
        String token = getToken();
        if (!token.isEmpty()) {
            params.put(TOKEN_PARAM_NAME, token);
        }
    }

    private String getToken() {
        SharedPreferences authPrefs = mContext.getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, Context.MODE_PRIVATE);
        return authPrefs.getString(LoginActivity.ACCESS_TOKEN, "");
    }
}

