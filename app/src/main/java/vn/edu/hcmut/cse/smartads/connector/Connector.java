package vn.edu.hcmut.cse.smartads.connector;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import vn.edu.hcmut.cse.smartads.listener.AdsContentListener;
import vn.edu.hcmut.cse.smartads.listener.ContextAdsReceivedListener;
import vn.edu.hcmut.cse.smartads.listener.LoginResponseListener;
import vn.edu.hcmut.cse.smartads.model.Ad;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by minhdaobui on 6/3/2015.
 */
public class Connector {
    private static Connector sInstance;
    private RequestQueue mRequestQueue;
    private static final String CONTEXT_ADS_BASE_URL = Config.HOST + "/customers/" + Config.CUSTOMER_ID + "/context-ads/";
    private static final String ADS_BASE_THUMBNAIL = Config.HOST + "/img/thumbnails/";
    private static final String ALL_ADS = Config.HOST + "/ads";
    public static final String LOGIN_URL = Config.HOST + "/auth/login";
    private static AtomicInteger queueCount = new AtomicInteger();

    public Connector(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized Connector getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Connector(context);
        }
        return sInstance;
    }

    public void requestContextAds(List<? extends Beacon> beaconList, final ContextAdsReceivedListener listener) {
        for (final Beacon beacon : beaconList) {
            final String url = CONTEXT_ADS_BASE_URL + beacon.getMinor();
            final List<Ad> contextAds = new ArrayList<>();

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    Log.d(Config.TAG, "minor " + beacon.getMinor() + " onResponse");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject adsObject;
                        try {
                            adsObject = jsonArray.getJSONObject(i);
                            contextAds.add(new Ad(adsObject.getInt("id"), adsObject.getString("title")));
                            Log.d(Config.TAG, String.format("Ad id = %d, Ad title = %s",
                                    adsObject.getInt("id"), adsObject.getString("title")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //requestImageThumbnail(contextAds, listener);
                    listener.onReceivedContextAds(contextAds);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(Config.TAG, "Connect to Server Error!" + url);
                }
            });

            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(4000,
                    5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(jsonArrayRequest);
            Log.d(Config.TAG, "minor " + beacon.getMinor() + " requested");


        }
    }

    public void requestAllAds(final AdsContentListener listener) {
        final String url = ALL_ADS;
        final List<Ad> allAds = new ArrayList<>();

        JsonArrayRequest addAdsRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonAd;
                            try {
                                jsonAd = jsonArray.getJSONObject(i);
                                allAds.add(new Ad(jsonAd.getInt("id"), jsonAd.getString("title")));
                                Log.d(Config.TAG, String.format("Ad id = %d, Ad title = %s",
                                        jsonAd.getInt("id"), jsonAd.getString("title")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.adsListChange(allAds);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(Config.TAG, "Connect to Server Error!" + url);
                    }
                });

        addAdsRequest.setRetryPolicy(new DefaultRetryPolicy(4000,
                5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(addAdsRequest);
        Log.d(Config.TAG, "All ads requested");

    }

    public void requestImageThumbnail(final List<Ad> adList, final AdsContentListener listener) {


        for (int i = 0; i < adList.size(); i++) {
            final int position = i;
            Log.d(Config.TAG, "Load image thumbnail position " + position);
            final Ad ad = adList.get(i);
            String url = ADS_BASE_THUMBNAIL + ad.getId() + ".png";
            Log.d(Config.TAG, "Load image thumbnail ID " + ad.getId());

            ImageRequest thumbnailRequest = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            ad.setIcon(bitmap);
                            listener.adsListUpdateImg(position);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            //doing nothing. Default icon will be displayed.
                        }
                    });
            thumbnailRequest.setRetryPolicy(new DefaultRetryPolicy(4000,
                    5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(thumbnailRequest);
        }


    }

    public void postLogin(String email, String password, final LoginResponseListener listener) {
        Map<String,String> params=new HashMap<>();
        params.put("email",email);
        params.put("password",password);

        CustomJsonObjectPostRequest request = new CustomJsonObjectPostRequest(LOGIN_URL,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(Config.TAG, "Login: onResponse" + jsonObject);
                if (!jsonObject.has("errors")) {
                    try {
                        String customerID=jsonObject.getString("customerID");
                        String accessToken=jsonObject.getString("accessToken");
                        listener.onSuccess(customerID,accessToken);
                    } catch (JSONException e) {
                        listener.onError(null);
                    }
                }
                else{
                    listener.onError("Wrong email or password");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(Config.TAG, "Login: onErrorResponse "+volleyError.getMessage());
                listener.onError(volleyError.getMessage());
            }
        });
        mRequestQueue.add(request);
        Log.d(Config.TAG, "Login request sent!");
    }
}
