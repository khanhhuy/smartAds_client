package ibeacon.smartadsv1.connector;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ibeacon.smartadsv1.model.Ad;
import ibeacon.smartadsv1.model.MyBeacon;
import ibeacon.smartadsv1.util.Config;

/**
 * Created by minhdaobui on 6/3/2015.
 */
public class Connector {
    private static Connector sInstance;
    private RequestQueue mRequestQueue;
    private static final String CONTEXT_ADS_BASE_URL = Config.HOST + "/customers/" + Config.CUSTOMER_ID + "/context-ads/";

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
            final String url=CONTEXT_ADS_BASE_URL+beacon.getMinor();
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    Log.d(Config.TAG, "minor "+beacon.getMinor() +" onResponse");
                    List<Ad> contextAds = new ArrayList<Ad>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject adsObject;
                        try {
                            adsObject = jsonArray.getJSONObject(i);
                            contextAds.add(new Ad(adsObject.getInt("id"), adsObject.getString("title")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listener.onReceivedContextAds(contextAds);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(Config.TAG, "Connect to Server Error!" + url);

                }
            });
            mRequestQueue.add(jsonArrayRequest);
            Log.d(Config.TAG,"minor " + beacon.getMinor() +" requested");
        }
    }
}