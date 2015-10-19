package vn.edu.hcmut.cse.smartads.settings.dev;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.reflect.Field;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.util.Config;

/**
 * Created by Minh Dao Bui on 10/19/2015.
 */
public class DevConfigEditTextPreference extends EditTextPreference {
    private Field mField;

    public DevConfigEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DevConfigEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mField = getFieldFromAttributeSet(context, attrs);

        setText(getFieldValue());
        updateSummary();
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
        setText(getFieldValue());
        updateSummary();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return getFieldValue();
    }

    private String getFieldValue() {
        try {
            return mField.get(null).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setFieldValue(String text) {
        try {
            Class<?> type = mField.getType();
            if (type.isAssignableFrom(String.class)) {
                mField.set(null, text);
            } else if (type.isAssignableFrom(Integer.TYPE)) {
                mField.set(null, Integer.parseInt(text));
            } else if (type.isAssignableFrom(Double.TYPE)) {
                mField.set(null, Double.parseDouble(text));
            } else if (type.isAssignableFrom(Boolean.TYPE)) {
                mField.set(null, Boolean.parseBoolean(text));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            setFieldValue(getText());
            updateSummary();
        }
    }


    private void updateSummary() {
        setSummary(getText());
    }


}
