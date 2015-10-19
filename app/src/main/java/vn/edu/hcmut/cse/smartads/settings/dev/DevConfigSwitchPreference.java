package vn.edu.hcmut.cse.smartads.settings.dev;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.reflect.Field;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Minh Dao Bui on 10/19/2015.
 */
public class DevConfigSwitchPreference extends SwitchPreference {
    private Field mField;


    public DevConfigSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DevConfigSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mField = getFieldFromAttributeSet(context, attrs);

        setChecked(getFieldValue());
    }

    protected Field getFieldFromAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DevConfigEditTextPreference);
        try {
            String fieldName = a.getString(R.styleable.DevConfigEditTextPreference_fieldName);
            if (!TextUtils.isEmpty(fieldName)) {
                return Config.class.getField(fieldName);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        return null;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(getFieldValue());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return getFieldValue();
    }

    private Boolean getFieldValue() {
        try {
            return mField.getBoolean(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void setFieldValue(Boolean value) {
        try {
            mField.setBoolean(null, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (getFieldValue() != checked) {
            setFieldValue(checked);
        }
        updateSummary();
    }

    private void updateSummary() {
        setSummary(getFieldValue().toString());
    }
}
