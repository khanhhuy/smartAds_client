package vn.edu.hcmut.cse.smartads.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import vn.edu.hcmut.cse.smartads.connector.Connector;

/**
 * Created by Huy on 5/1/2015.
 */
public class Config {
    public static int BEACON_MIN_RECEIVED_TIME_SEC = 10;
    public static int SERVER_UPDATE_REQUEST_MIN_SEC = 7;//12*3600 = 43200
    public static int JUST_RECEIVED_TIME_HOUR = 2;
    public static int DELAY_RANGING_SLOW_MIN = 15;
    public static int DELAY_MONITORING_HOUR = 1;
    public static int MIN_NOTIFICATION_SOUND_DELAYED_SEC = 2;
    public static int MONITORING_SLEEP_PERIOD_SEC = 60;//60

    public static String HOST_BASE = "http://smartads.esy.es";
//    public static String HOST_BASE = "http://smartads.byethost7.com";
//  public static String HOST_BASE = "http://192.168.43.159/ttlvserver/public";
//    public static String HOST_BASE = "http://192.168.1.110/ttlvserver/public";
//    public static String HOST_BASE = "http://192.168.1.2:8000";

    public static String HOST_API = HOST_BASE + "/api/v1";
    public static String APP_NAME = "Smart Ads";

    public static String TAG = "DHSmartAds";
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_PATTERN = "yyyy-MM-dd";
    public static String DATE_DISPLAY_PATTERN = "dd/MM/yy";

    public static void updateHost() {
        HOST_API = HOST_BASE + "/api/v1";
        Connector.updateURL();
    }

    public static final HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<String, LatLng>();

    static {
        AREA_LANDMARKS.put("HUY_HOUSE", new LatLng(10.850651, 106.750741));
    }

    public static final float GEOFENCE_RADIUS_IN_METERS = 200;
    public static final int GEOFENCE_LOITERING_IN_MINUTES = 1 * 60 * 1000;

    /*
    DEBUG = true
    - Notify multiple time
    - Flush Database ( onCreate - ContextAdsService )
    - SERVER_GET_ADS_MIN_HOUR = 0
    DEBUG = false
    - Remember to set SERVER_GET_ADS_MIN_HOUR = 1
     */
    public static boolean DEBUG = true;
}
