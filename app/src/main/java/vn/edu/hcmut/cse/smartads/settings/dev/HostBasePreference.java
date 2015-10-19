package vn.edu.hcmut.cse.smartads.settings.dev;

import android.content.Context;
import android.util.AttributeSet;

import java.lang.reflect.Field;

import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Minh Dao Bui on 10/19/2015.
 */
public class HostBasePreference extends DevConfigEditTextPreference {
    public HostBasePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HostBasePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Field getFieldFromAttributeSet(Context context, AttributeSet attrs) {
        try {
            return Config.class.getField("HOST_BASE");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void setFieldValue(String text) {
        super.setFieldValue(text);
        Config.updateHost();
    }
}
