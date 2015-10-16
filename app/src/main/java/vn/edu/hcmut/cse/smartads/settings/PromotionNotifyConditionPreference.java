package vn.edu.hcmut.cse.smartads.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import vn.edu.hcmut.cse.smartads.R;

/**
 * Created by Minh Dao Bui on 10/16/2015.
 */
public class PromotionNotifyConditionPreference extends DialogPreference {
    private EditText mEditMinDiscountValue;
    private SeekBar mSeekMinDiscountRate;
    private TextView mTextMinDiscountRate;
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
        mEditMinDiscountValue= (EditText) view.findViewById(R.id.setting_edit_discount_value);
        mTextMinDiscountRate=(TextView) view.findViewById(R.id.setting_text_discount_rate);
        mSeekMinDiscountRate= (SeekBar) view.findViewById(R.id.setting_seek_discount_rate);
        mSeekMinDiscountRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextMinDiscountRate.setText(String.valueOf(progress)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
