package vn.edu.hcmut.cse.smartads.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.ChangePassResponseListener;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.util.Utils;

public class ChangePassActivity extends AppCompatActivity implements ChangePassResponseListener {
    private EditText mCurrentPass, mNewPass, mConfirmNewPass;
    private EditText[] mInputs;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Button btnChange = (Button) findViewById(R.id.cp_btn_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pass = validateInput();
                if (pass) {
                    attemptChangePass();
                }
            }
        });
        mCurrentPass = (EditText) findViewById(R.id.cp_current_pass);
        mNewPass = (EditText) findViewById(R.id.cp_new_pass);
        mConfirmNewPass = (EditText) findViewById(R.id.cp_new_pass_confirm);
        mInputs = new EditText[]{mCurrentPass, mNewPass, mConfirmNewPass};
        mProgressDialog = Utils.createLoadingDialog(this);
    }


    private boolean validateInput() {
        for (EditText input : mInputs) {
            if (input.getText() == null || input.getText().length() <= 0) {
                input.setError(getString(R.string.error_field_required));
            }
        }
        for (EditText input : new EditText[]{mCurrentPass, mNewPass}) {
            if (!hasError(input) && !Utils.isPasswordValid(input.getText().toString())) {
                input.setError(getString(R.string.error_invalid_password));
            }
        }

        if (!hasError(mNewPass) && !hasError(mConfirmNewPass)) {
            if (!mNewPass.getText().toString().equals(mConfirmNewPass.getText().toString())) {
                mConfirmNewPass.setError(getString(R.string.error_confirm_not_match));
            }

        }

        for (EditText input : mInputs) {
            if (hasError(input)) {
                input.requestFocus();
                return false;
            }
        }

        return true;
    }

    private static boolean hasError(EditText input) {
        return !TextUtils.isEmpty(input.getError());
    }

    private void attemptChangePass() {
        mProgressDialog.show();
        Connector.getInstance(this).postChangePass(Utils.getCustomerID(this), mCurrentPass.getText().toString(),
                mNewPass.getText().toString(), this);
    }

    @Override
    public void onWrongCurrentPasswordError() {
        mProgressDialog.dismiss();
        mCurrentPass.setError(getString(R.string.error_incorrect_current_pass));
        mCurrentPass.requestFocus();
    }

    @Override
    public void onSuccess() {
        mProgressDialog.dismiss();
        Toast.makeText(this, R.string.success_change_pass, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onError(String message) {
        Utils.hideProgressAndShowError(mProgressDialog, message);
    }
}
