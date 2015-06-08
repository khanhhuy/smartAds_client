package ibeacon.smartadsv1.connector;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ibeacon.smartadsv1.model.Ad;
import ibeacon.smartadsv1.util.Config;

/**
 * Created by minhdaobui on 6/3/2015.
 */
public class Connector {
    private static Connector sInstance;
    private RequestQueue mRequestQueue;
    private static final String CONTEXT_ADS_BASE_URL = Config.HOST + "/customers/" + Config.CUSTOMER_ID + "/context-ads/";
    private static final String ADS_BASE_THUMBNAIL = Config.HOST + "/img/thumbnails/";
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

    private void requestImageThumbnail(final List<Ad> contextAdsList, final ContextAdsReceivedListener listener) {


        for (final Ad contextAd : contextAdsList) {
            String url = ADS_BASE_THUMBNAIL + contextAd.getId() + ".png";

            ImageRequest thumbnailRequest = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            contextAd.setIcon(Bitmap.createScaledBitmap(bitmap, 48, 48, false));
                            queueCount.addAndGet(-1);
                            if (queueCount.get() == 0)
                                listener.onReceivedContextAds(contextAdsList);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            //doing nothing. Default icon will be displayed.
                            queueCount.addAndGet(-1);
                            if (queueCount.get() == 0)
                                listener.onReceivedContextAds(contextAdsList);
                        }
                    });
            mRequestQueue.add(thumbnailRequest);
            queueCount.addAndGet(1);
        }
    }
}
