package vn.edu.hcmut.cse.smartads.util;

import vn.edu.hcmut.cse.smartads.connector.Connector;

/**
 * Created by Huy on 5/1/2015.
 */
public class Config {
    public static int BEACON_MIN_RECEIVED_TIME_SEC = 10;
    public static int SERVER_UPDATE_REQUEST_MIN_DATE = 0;
    public static int JUST_RECEIVED_TIME_HOUR = 2;
    public static int MIN_NOTIFICATION_SOUND_DELAYED_SEC = 10;

    public static int MIN_RECEIVED_TIME_SEC = 15;
//  public static String HOST_BASE = "http://smartads.esy.es";
//  public static String HOST_BASE = "http://smartads.byethost7.com";
//  public static String HOST_BASE = "http://192.168.43.159/ttlvserver/public";
//  public static String HOST_BASE = "http://192.168.1.104/ttlvserver/public";
    public static String HOST_BASE = "http://192.168.1.2:8000";
//public static String HOST_BASE = "http://192.168.1.102/ttlvserver/public";
    
    public static String HOST_API = HOST_BASE + "/api/v1";
    public static String APP_NAME = "Smart Ads";

    public static String TAG = "DHSmartAds";
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_PATTERN = "yyyy-MM-dd";

    public static void updateHost() {
        HOST_API = HOST_BASE + "/api/v1";
        Connector.updateURL();
    }

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
