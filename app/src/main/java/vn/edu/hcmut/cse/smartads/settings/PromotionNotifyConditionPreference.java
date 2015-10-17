package vn.edu.hcmut.cse.smartads.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import vn.edu.hcmut.cse.smartads.R;

/**
 * Created by Minh Dao Bui on 10/16/2015.
 */
public class PromotionNotifyConditionPreference extends DialogPreference {
    private EditText mEditMinDiscountValue;
    private NumberPicker mPickerMinDiscountRate;
    private int mDefaultRate;
    private BigDecimal mDefaultValue;

    public PromotionNotifyConditionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_promotion_notify_condition);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);

        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.PromotionNotifyConditionPreference,0,0);
        try{
            mDefaultRate = a.getInteger(R.styleable.PromotionNotifyConditionPreference_defaultRate,20);
            mDefaultValue=new BigDecimal(a.getFloat(R.styleable.PromotionNotifyConditionPreference_defaultValue,15000));
        }
        finally {
            a.recycle();
        }
    }

    protected boolean needInputMethod() {
        return true;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mEditMinDiscountValue = (EditText) view.findViewById(R.id.setting_edit_discount_value);
        mEditMinDiscountValue.setText(mDefaultValue.toString());
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
        mPickerMinDiscountRate.setValue(mDefaultRate);
    }
}
