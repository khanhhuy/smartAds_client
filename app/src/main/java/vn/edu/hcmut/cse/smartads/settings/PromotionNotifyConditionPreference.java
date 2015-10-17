package vn.edu.hcmut.cse.smartads.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.util.Utils;

/**
 * Created by Minh Dao Bui on 10/16/2015.
 */
public class PromotionNotifyConditionPreference extends DialogPreference {
    public static final String DELIMITER = " ";
    private EditText mEditMinDiscountValue;
    private NumberPicker mPickerMinDiscountRate;
    private int mDiscountRate;
    private BigDecimal mDiscountValue;

    public PromotionNotifyConditionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_promotion_notify_condition);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    protected boolean needInputMethod() {
        return true;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mEditMinDiscountValue = (EditText) view.findViewById(R.id.setting_edit_discount_value);
        mEditMinDiscountValue.setText(mDiscountValue.toString());
        mPickerMinDiscountRate = (NumberPicker) view.findViewById(R.id.setting_picker_discount_rate);
        Field f = null;
        try {
            f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = null;
            inputText = (EditText) f.get(mPickerMinDiscountRate);
            inputText.setFilters(new InputFilter[0]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        mPickerMinDiscountRate.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value) + " %";
            }
        });
        mPickerMinDiscountRate.setMinValue(0);
        mPickerMinDiscountRate.setMaxValue(100);
        mPickerMinDiscountRate.setValue(mDiscountRate);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            parseStringToDefault(getPersistedString(getContext().getString(R.string.settings_def_entrance)));
        } else {
            parseStringToDefault((String) defaultValue);
        }
        updateSummary();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mDiscountRate = mPickerMinDiscountRate.getValue();
            String discountValue = mEditMinDiscountValue.getText().toString();
            mDiscountValue = new BigDecimal(discountValue);
            persistString(mDiscountRate
                    + DELIMITER + discountValue);

            updateSummary();
        }
    }

    private void updateSummary() {
        String value = Utils.currencyFormat(mDiscountValue);
        setSummary(getContext().getString(R.string.settings_condition_summary, mDiscountRate, value));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    private void parseStringToDefault(String s) {
        String[] defaults = s.split(DELIMITER);
        mDiscountRate = Integer.parseInt(defaults[0]);
        mDiscountValue = new BigDecimal(defaults[1]);
    }
}
