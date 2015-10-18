package vn.edu.hcmut.cse.smartads.connector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.activity.LoginActivity;
import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.model.image.ImageCacheManager;
import vn.edu.hcmut.cse.smartads.settings.SettingsResponseListener;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Minh Dao Bui on 6/3/2015.
 */
public class Connector {
    private static Connector sInstance;
    public static final String CUSTOMER_URL = Config.HOST_API + "/customers/";
    public static final String CONTEXT_ADS_BASE_URL = Config.HOST_API + "/customers/";
    public static final String ADS_BASE_THUMBNAIL = Config.HOST_PORTAL + "/ads/thumbnail/";
    public static final String LOGIN_URL = Config.HOST_API + "/auth/login";
    public static final String ACCOUNT_STATUS_URL = Config.HOST_API + "/account-status?email=%s";
    public static final String REGISTER_URL = Config.HOST_API + "/auth/register";

    public static final String PREF_TIME  = "prefTime";
    public static final String UPDATE_REQUEST_DATE  = "updateRequestDate";

    public static final String SETTINGS_URL = Config.HOST_API + "/customers/%s/config";

    private final Context mContext;
    private RequestQueue mRequestQueue;
    private ImageCacheManager imageManager;

    private Connector(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        //mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(mContext));
        imageManager = ImageCacheManager.getInstance();
        Log.d(Config.TAG, "Unique name" + mContext.getPackageCodePath());
        imageManager.init(mContext, mRequestQueue, mContext.getPackageCodePath(),
                ImageCacheManager.DISK_IMAGECACHE_SIZE, ImageCacheManager.DISK_IMAGECACHE_COMPRESS_FORMAT,
                ImageCacheManager.DISK_IMAGECACHE_QUALITY, ImageCacheManager.CacheType.DISK);
    }

    public static synchronized Connector getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Connector(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageManager.getImageLoader();
    }


    public void requestContextAds(final List<MyBeacon> beacons, final ContextAdsReceivedListener listener) {

        if (beacons.isEmpty())
            return;

        MyBeacon beacon = beacons.get(0);

        SharedPreferences authPrefs = mContext.getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, Context.MODE_PRIVATE);
        String customerID = authPrefs.getString(LoginActivity.CUSTOMER_ID, "");
        if (customerID.isEmpty())
            return;

        final String url = CUSTOMER_URL + customerID + "/context-ads/" + beacon.getMajor() + "/" + beacon.getMinor();
        JsonObjectRequest contextAdsRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonContextAds) {
                try {
                    parseAds(jsonContextAds);
                    Log.d(Config.TAG, "DB Ads size" + Ads.listAll(Ads.class).size());

                    listener.onReceivedContextAds(beacons);

                    imageManager.clearCache();

                    if (Config.DEBUG)
                        logDB();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "Connect to Server Error!" + url);
            }
        });

        contextAdsRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(contextAdsRequest);
    }

    private void parseAds(JSONObject jsonMixedAds) throws JSONException {
        String[] adsType = new String[]{Ads.ENTRANCE_ADS, Ads.AISLE_ADS};
        for (String type : adsType) {
            JSONArray adsGroup = jsonMixedAds.getJSONArray(type);

            for (int i = 0; i < adsGroup.length(); i++) {
                JSONObject ads = adsGroup.getJSONObject(i);

                //parse minors
                List<Integer> minors = null;
                if (!ads.isNull(Ads.MINORS)) {
                    JSONArray minorsJSON = ads.getJSONArray(Ads.MINORS);
                    minors = new ArrayList<>();
                    for (int j = 0; j < minorsJSON.length(); j++) {
                        minors.add(minorsJSON.getInt(j));
                    }
                }

                //parse date
                DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATE_PATTERN);
                DateTime startDate, endDate;
                if (ads.isNull(Ads.START_DATE))
                    startDate = null;
                else
                    startDate = DateTime.parse(ads.getString(Ads.START_DATE), formatter);

                if (ads.isNull(Ads.END_DATE))
                    endDate = null;
                else
                    endDate = DateTime.parse(ads.getString(Ads.END_DATE), formatter);

                //create new Ads
                Ads newAds = new Ads(
                        Integer.parseInt(ads.getString(Ads.ID)), ads.getString(Ads.TITLE),
                        startDate, endDate, minors, type);

                newAds.InsertOrUpdate();
            }
        }
    }

    public void postLogin(String email, String password, final LoginResponseListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        CustomJsonObjectPostRequest request = new CustomJsonObjectPostRequest(LOGIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(Config.TAG, "Login: onResponse" + jsonObject);
                if (!jsonObject.has("errors")) {
                    try {
                        String customerID = jsonObject.getString("customerID");
                        String accessToken = jsonObject.getString("accessToken");
                        listener.onSuccess(customerID, accessToken);
                    } catch (JSONException e) {
                        listener.onError(null);
                    }
                } else {
                    listener.onError(mContext.getString(R.string.error_login_incorrect));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "Login: onErrorResponse " + volleyError.getMessage());
                String message = getNetworkErrorMessage(volleyError);
                listener.onError(message);
            }
        });
        mRequestQueue.add(request);
        Log.d(Config.TAG, "Login request sent!");
    }

    private String getNetworkErrorMessage(VolleyError volleyError) {
        String message = mContext.getString(R.string.error_network_problem);
        if (volleyError.getMessage() != null) {
            message += System.getProperty("line.separator") + "(" +
                    volleyError.getMessage() + ")";
        }
        return message;
    }

    public void requestAccountStatus(String email, final AccountStatusResponseListener listener) {
        String url = String.format(ACCOUNT_STATUS_URL, email);
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!jsonObject.has("errors")) {
                    try {
                        String accountStatus = jsonObject.getString("result");
                        listener.onSuccess(accountStatus);
                    } catch (JSONException e) {
                        listener.onError(null);
                    }
                } else {
                    listener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "Login: onErrorResponse " + volleyError.getMessage());
                String message = getNetworkErrorMessage(volleyError);
                listener.onError(message);
            }
        });
        mRequestQueue.add(request);
        Log.d(Config.TAG, "account status request sent!");
    }

    public void register(String email, String password, final SimpleResponseListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        CustomJsonObjectPostRequest request = new CustomJsonObjectPostRequest(REGISTER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(Config.TAG, "Login: onResponse" + jsonObject);
                if (!jsonObject.has("errors")) {
                    try {
                        boolean result = jsonObject.getBoolean("result");
                        if (result) {
                            listener.onSuccess();
                        } else {
                            listener.onError(null);
                        }
                    } catch (JSONException e) {
                        listener.onError(null);
                    }
                } else {
                    listener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "Login: onErrorResponse " + volleyError.getMessage());
                String message = getNetworkErrorMessage(volleyError);
                listener.onError(message);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
        Log.d(Config.TAG, "Register request sent!");
    }

    public void updateRequest() {

        SharedPreferences authPrefs = mContext.getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, Context.MODE_PRIVATE);
        String customerID = authPrefs.getString(LoginActivity.CUSTOMER_ID, "");
        if (customerID.isEmpty())
            return;
        Log.d(Config.TAG, "Update request starts");
        String url = CUSTOMER_URL + customerID + "/update-request";
        StringRequest postUpdateRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Config.TAG, "Update request completed");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });

        postUpdateRequest.setRetryPolicy(new DefaultRetryPolicy(4000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(postUpdateRequest);
    }

    //Debug and testing function
    public void logDB() {
        Iterator<Ads> iterator = Ads.findAll(Ads.class);
        while (iterator.hasNext()) {
            Ads ads = iterator.next();
            Log.d(Config.TAG, "Ads id = " + ads.getAdsId() + " title " + ads.getTitle() +
                            " startDate = " + ads.getStartDate() + " endDate = " + ads.getEndDate() +
                            " lastUpdated = " + ads.getLastUpdated() + " isNotified = " + ads.is_notified() +
                            " isViewed = " + ads.is_viewed() + " isBlacklisted = " + ads.is_blacklisted() +
                            " type = " + ads.getType()
            );
            List<Integer> minors = ads.getMinors();
            Log.d(Config.TAG, "minors size = " + minors.size());
            if (!minors.isEmpty()) {
                Log.d(Config.TAG, "minors  = " + minors.toString());
            }

        }
    }


    public void requestSettings(String customerID, final SettingsResponseListener listener) {
        String url = String.format(SETTINGS_URL, customerID);
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (!jsonObject.has("errors")) {
                    Integer entranceRate;
                    try {
                        entranceRate = (int) Math.floor(jsonObject.getDouble("min_entrance_rate"));
                    } catch (Exception e) {
                        entranceRate = null;
                    }

                    BigDecimal entranceValue;
                    try {
                        entranceValue = new BigDecimal(jsonObject.getDouble("min_entrance_value"));
                    } catch (Exception e) {
                        entranceValue = null;
                    }


                    Integer aisleRate;
                    try {
                        aisleRate = (int) Math.floor(jsonObject.getDouble("min_aisle_rate"));
                    } catch (Exception e) {
                        aisleRate = null;
                    }

                    BigDecimal aisleValue;
                    try {
                        aisleValue = new BigDecimal(jsonObject.getDouble("min_aisle_value"));
                    } catch (Exception e) {
                        aisleValue = null;
                    }

                    listener.onSuccess(entranceRate, entranceValue, aisleRate, aisleValue);
                } else {
                    listener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(null);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 1.0F));
        mRequestQueue.add(request);
        Log.d(Config.TAG, "requestSettings request sent!");
    }

    public void updateSettings(String customerID, Map<String, String> settings,final SimpleResponseListener listener) {
        String url = String.format(SETTINGS_URL,customerID);
        CustomJsonObjectPostRequest request = new CustomJsonObjectPostRequest(url, settings, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(Config.TAG, "updateSettings: onResponse" + jsonObject);
                if (!jsonObject.has("errors")) {
                    listener.onSuccess();
                } else {
                    listener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "updateSettings: onErrorResponse " + volleyError.getMessage());
                String message = getNetworkErrorMessage(volleyError);
                listener.onError(message);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
        Log.d(Config.TAG, "updateSettings request sent!");
    }
}
