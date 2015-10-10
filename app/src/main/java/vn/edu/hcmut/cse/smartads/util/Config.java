package vn.edu.hcmut.cse.smartads.util;

/**
 * Created by Huy on 5/1/2015.
 */
public class Config {
    public static int BEACON_MIN_RECEIVED_TIME_SEC = 10;
    public static int SERVER_MIN_UPDATE_TIME_HOUR = 0;

    //public static String HOST = "http://smartads.esy.es";
    //public static String HOST = "http://smartads.byethost7.com";

    //public static String HOST = "http://192.168.1.104/ttlvserver/public";
    public static String HOST = "http://192.168.1.2:8000";
    public static String APP_NAME = "Smart Ads";

    public static int CUSTOMER_ID = 1;
    public static String TAG = "DHSmartAds";
    public static String DATE_PATTERN="yyyy-MM-dd HH:mm:ss";

    /*
    DEBUG = true
    - Notify multiple time
    - Flush Database ( onCreate - ContextAdsService )
    - Remember to set SERVER_MIN_UPDATE_TIME_HOUR = 0
    DEBUG = false
    - Remember to set SERVER_MIN_UPDATE_TIME_HOUR = 1
     */
    public static boolean DEBUG = true;
}
