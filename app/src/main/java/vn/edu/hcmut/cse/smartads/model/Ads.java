package vn.edu.hcmut.cse.smartads.model;

import android.graphics.Bitmap;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Huy on 5/14/2015.
 */
public class Ads extends SugarRecord<Ads> {

    @Ignore
    public static final String ENTRANCE_ADS  = "entranceAds";
    @Ignore
    public static final String AISLE_ADS  = "aisleAds";
    @Ignore
    public static final String ID = "id";
    @Ignore
    public static final String TITLE = "title";
    @Ignore
    public static final String START_DATE = "start_date";
    @Ignore
    public static final String END_DATE = "end_date";
    @Ignore
    public static final String MINORS = "minors";
    @Ignore
    public static final String TARGETED_ADS = "targetedAds";

    //Persistence
    private int adsId;
    private String title;
    private String startDate;
    private String endDate;
    private String lastUpdated;
    private String lastReceived;
    private boolean isNotified;
    private boolean isViewed;
    private boolean isBlacklisted;
    private String type;

    @Ignore
    private Bitmap icon;
    @Ignore
    private List<Integer> minors;

    public Ads() {
    }

    public Ads(int adsId, String title, DateTime startDate, DateTime endDate, List<Integer> minors, String type) {
        this.adsId = adsId;
        this.title = title;
        this.startDate = parserDateToString(startDate);
        this.endDate = parserDateToString(endDate);
        this.type = type;
        this.minors = minors;
    }

    static public Ads findAds(String adsId) {
        List<Ads> existedAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
        if (existedAds.size() == 0)
            return null;
        else
            return existedAds.get(0);

    }

    public void InsertOrUpdate() {
        List<Ads> existedAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", String.valueOf(this.getAdsId())));


        if (existedAds.size() == 0) {
            this.setLastUpdated(new DateTime());
            //this.setLastReceived(new DateTime());
            this.save();
        } else {
            existedAds.get(0).setTitle(this.getTitle());
            existedAds.get(0).setStartDate(this.getStartDate());
            existedAds.get(0).setEndDate(this.getEndDate());
            existedAds.get(0).setLastUpdated(new DateTime());
            existedAds.get(0).setLastReceived(this.getLastReceived());
            existedAds.get(0).setViewed(this.isViewed);
            existedAds.get(0).setNotified(this.isNotified);
            existedAds.get(0).setBlacklisted(this.isBlacklisted);

            existedAds.get(0).save();
        }

        List<Minor> existedMinors = Minor.find(Minor.class, "ads = ?", String.valueOf(this.getId()));
        for (Minor existedMinor : existedMinors) {
            existedMinor.delete();
        }

        if (minors != null && !minors.isEmpty()) {
            for (Integer newMinorId : minors) {
                Minor newMinor = new Minor(newMinorId);
                newMinor.setAds(this);
                newMinor.save();
            }
        }

    }

    static public boolean isExistedAds(String adsId) {
        List<Ads> existedAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
        return existedAds.size() != 0;
    }

    static public String parserDateToString(DateTime dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATE_PATTERN);
        return formatter.print(dateTime);
    }
    static public DateTime parseStringToDate(String dateTime) {
        if (dateTime == null || dateTime.isEmpty())
            return null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATE_PATTERN);
        return DateTime.parse(dateTime, formatter);
    }
    static public String parserDateTimeToString(DateTime dateTime) {
        if (dateTime == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATETIME_PATTERN);
        return formatter.print(dateTime);
    }
    static public DateTime parseStringToDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty())
            return null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATETIME_PATTERN);
        return DateTime.parse(dateTime, formatter);
    }

    public int getAdsId() {
        return adsId;
    }
    public void setAdsId(int adsId) {
        this.adsId = adsId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getStartDate() {
        return parseStringToDate(startDate);
    }
    public void setStartDate(DateTime startDate) {
        this.startDate = parserDateToString(startDate);
    }
    public DateTime getEndDate() {
        return parseStringToDate(endDate);
    }
    public void setEndDate(DateTime endDate) {
        this.endDate = parserDateToString(endDate);
    }
    public DateTime getLastUpdated() {
        return parseStringToDateTime(lastUpdated);
    }
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = parserDateTimeToString(lastUpdated);
    }
    public DateTime getLastReceived() {
        return parseStringToDateTime(lastReceived);
    }
    public void setLastReceived(DateTime lastReceived) {
        this.lastReceived = parserDateTimeToString(lastReceived);
    }

    public void setMinors(List<Integer> minors) {
        this.minors = minors;
    }
    public List<Integer> getMinors() {

        List<Minor> existedMinors = Minor.find(Minor.class, "ads = ?", String.valueOf(this.getId()));
        List<Integer> minors = new ArrayList<>();
        for (Minor m : existedMinors) {
            minors.add(m.getMinor());
        }

        this.setMinors(minors);

        return minors;
    }

    public boolean is_notified() {
        return isNotified;
    }
    public void setNotified(boolean notified) {
        this.isNotified = notified;
    }
    public boolean is_viewed() {
        return isViewed;
    }
    public void setViewed(boolean viewed) {
        this.isViewed = viewed;
    }
    public boolean is_blacklisted() {
        return isBlacklisted;
    }
    public void setBlacklisted(boolean blacklisted) {
        this.isBlacklisted = blacklisted;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getIcon() {
        return icon;
    }
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
