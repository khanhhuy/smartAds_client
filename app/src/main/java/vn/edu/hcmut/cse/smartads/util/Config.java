package vn.edu.hcmut.cse.smartads.util;

/**
 * Created by Huy on 5/1/2015.
 */
public class Config {
    public static int BEACON_MIN_RECEIVED_TIME_SEC = 10;
    public static int SERVER_MIN_UPDATE_TIME_HOUR = 0;
    public static int JUST_RECEIVED_TIME_HOUR = 2;
    public static int MIN_NOTIFICATION_SOUND_DELAYED_SEC = 10;

    public static int MIN_RECEIVED_TIME_SEC = 15;
//  public static String HOST_PORTAL = "http://smartads.esy.es";
//  public static String HOST_PORTAL = "http://smartads.byethost7.com";
    public static String HOST_PORTAL = "http://192.168.43.159/ttlvserver/public";
//    public static String HOST_PORTAL = "http://192.168.1.2:8000";
    public static String HOST_API = HOST_PORTAL+"/api/v1";
    public static String APP_NAME = "Smart Ads";

    public static String TAG = "DHSmartAds";
    public static String DATETIME_PATTERN ="yyyy-MM-dd HH:mm:ss";
    public static String DATE_PATTERN ="yyyy-MM-dd";

    /*
    DEBUG = true
    - Notify multiple time
    - Flush Database ( onCreate - ContextAdsService )
    - SERVER_MIN_UPDATE_TIME_HOUR = 0
    DEBUG = false
    - Remember to set SERVER_MIN_UPDATE_TIME_HOUR = 1
     */
    public static boolean DEBUG = true;
}
