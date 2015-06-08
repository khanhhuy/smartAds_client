package ibeacon.smartadsv1.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Huy on 5/14/2015.
 */
public class Ad {
    private int id;
    private String title;
    private String description;
    private Date expire_date;
    private Date last_updated;
    private Bitmap icon = null;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String EXPIRE_DATE = "expire_date";
    public static final String LAST_UPDATED = "last_updated";

    public Ad() {

    }

    public Ad(int id, String title, String description, Date expire_date, Date last_updated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expire_date = expire_date;
        this.last_updated = last_updated;
    }

    public Ad(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
