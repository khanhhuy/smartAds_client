package ibeacon.smartadsv1.old;

import android.os.*;
import android.os.Process;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ibeacon.smartadsv1.model.Ad;
import ibeacon.smartadsv1.model.MyBeacon;
import ibeacon.smartadsv1.util.Config;
import ibeacon.smartadsv1.util.MessageDefined;

/**
 * Created by Huy on 5/10/2015.
 */
public class ServerHandlerThread extends HandlerThread {

    private Handler mResponseHandler;
    private Handler mServerHandler;
    private IOperationCallback mOperationCallback;
    public final static int GET = 1;
    public final static int POST = 2;


    public ServerHandlerThread(Handler responseHandler, IOperationCallback operationCallback) {
        super("ServerOperation", Process.THREAD_PRIORITY_BACKGROUND);
        mResponseHandler = responseHandler;
        mOperationCallback = operationCallback;
    }

    public void getContextAdsTask(List<MyBeacon> myBeaconList) {
        Message msg = mServerHandler.obtainMessage(MessageDefined.GET_CUSTOMER_CONTEXTADS, myBeaconList);
        mServerHandler.sendMessage(msg);
    }

    public void prepareHandler() {
        mServerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch (msg.what) {
                    case MessageDefined.GET_CUSTOMER_CONTEXTADS:

                        final List<Ad> contextAdList = getContextAds((List<MyBeacon>) msg.obj);

                        if (contextAdList.size() > 0){
                            mResponseHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mOperationCallback.receivedContextAds(contextAdList);
                                }
                            });
                        }

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    private List<Ad> getContextAds(List<MyBeacon> myBeaconList){

        String urlPath = Config.HOST + "/customers/" + Config.CUSTOMER_ID + "/context-ads/";
        InputStream is = null;
        List<Ad> adContextList = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();

        try {
            for (MyBeacon beacon : myBeaconList) {
                is = openConnection(urlPath + String.format("%d", beacon.getMinor()));
                if (is != null) {
                    String jsonAdList = getJSONstring(is);
                    JsonElement element = jsonParser.parse(jsonAdList);
                    if (element.isJsonArray()) {
                        JsonArray adList = element.getAsJsonArray();
                        for (int i = 0; i < adList.size(); i++) {
                            JsonObject jsonAd = adList.get(i).getAsJsonObject();
                            if (jsonAd.isJsonObject()) {
                                Ad newAd = new Ad();

                                newAd.setId(jsonAd.get(Ad.ID).getAsInt());
                                newAd.setTitle(jsonAd.get(Ad.TITLE).getAsString());

                                //Todo: Parse date.

                                adContextList.add(newAd);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return adContextList;
    }

    private String getJSONstring(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
        }
        String jsonString = builder.toString();

        return jsonString;
    }

    private InputStream openConnection(String urlPath) throws IOException {

        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        int response = conn.getResponseCode();
        Log.d("Response from Server", "The response is: " + response);
        if (response != HttpURLConnection.HTTP_OK) {
            return null;
        }

        InputStream is = conn.getInputStream();

        return is;
    }
}
