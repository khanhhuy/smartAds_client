package vn.edu.hcmut.cse.smartads.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewAnimator;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.AccountStatusResponseListener;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.connector.RegisterResponseListener;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

public class SignUpActivity extends AppCompatActivity {
    private ViewAnimator mViewAnimator;
    private EditText mEmailEdit, mPasswordEdit, mConfirmPasswordEdit;
    private String mEmail;
    private static final String NOT_A_MEMBER = "NOT_A_MEMBER", DONT_HAVE_PASSWORD = "DONT_HAVE_PASSWORD",
            HAVE_PASSWORD = "HAVE_PASSWORD";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mViewAnimator = (ViewAnimator) findViewById(R.id.signup_view_switcher);
        mEmailEdit = (EditText) findViewById(R.id.signup_edit_email);
        Button btnNext = (Button) findViewById(R.id.btn_next_signup);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountStatus();
            }
        });
        Button btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        mPasswordEdit = (EditText) findViewById(R.id.signup_edit_password);
        mConfirmPasswordEdit = (EditText) findViewById(R.id.signup_edit_confirm_password);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please Wait..");
    }

    private void attemptSignUp() {
        String password = mPasswordEdit.getText().toString();
        String confirm = mConfirmPasswordEdit.getText().toString();
        View focusView = null;
        boolean cancel = false;

        for (EditText e : (new EditText[]{mPasswordEdit, mConfirmPasswordEdit})) {
            if (TextUtils.isEmpty(e.getText().toString())) {
                cancel = true;
                e.setError(getString(R.string.error_field_required));
                if (focusView == null) {
                    focusView = e;
                }
            }
        }
        if (!cancel && password.length() < 5) {
            cancel = true;
            mPasswordEdit.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEdit;
        }
        if (!cancel && !confirm.equals(password)) {
            cancel = true;
            mConfirmPasswordEdit.setError(getString(R.string.error_confirm_not_match));
            focusView = mConfirmPasswordEdit;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            hideSoftInput();
            mProgressDialog.setMessage("Signing up...");
            mProgressDialog.show();
            Connector.getInstance(this).register(mEmail, password, new RegisterResponseListener() {
                @Override
                public void onSuccess() {
                    mProgressDialog.dismiss();
                    showSuccessPage();
                }

                @Override
                public void onError(String message) {
                    hideProgressAndShowError(message);
                }
            });
        }
    }

    private void hideSoftInput() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSuccessPage() {
        mViewAnimator.showNext();
        mViewAnimator.removeViews(0, 2);
        mViewAnimator.setDisplayedChild(0);
    }

    private void checkAccountStatus() {
        mEmail = mEmailEdit.getText().toString();
        boolean cancel = false;
        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailEdit.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!Utils.isValidEmail(mEmail)) {
            mEmailEdit.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }
        if (cancel) {
            mEmailEdit.requestFocus();
        } else {
            mProgressDialog.setMessage("Checking Email...");
            mProgressDialog.show();
            Connector.getInstance(this).requestAccountStatus(mEmail, new AccountStatusResponseListener() {
                @Override
                public void onSuccess(String accountStatus) {
                    mProgressDialog.dismiss();
                    accountStatus = accountStatus.toUpperCase();
                    Log.d(Config.TAG, accountStatus);
                    switch (accountStatus) {
                        case DONT_HAVE_PASSWORD:
                            mViewAnimator.showNext();
                            break;
                        case HAVE_PASSWORD:
                            Utils.showAlertDialog(SignUpActivity.this,
                                    getString(R.string.error_already_registed_title),
                                    getString(R.string.error_already_registed));
                            break;
                        case NOT_A_MEMBER:
                            Utils.showAlertDialog(SignUpActivity.this, getString(R.string.error_not_member_title),
                                    getString(R.string.error_not_member));
                            break;
                        default:
                            Utils.showAlertDialog(SignUpActivity.this,
                                    getString(R.string.error_unkown));
                            break;
                    }
                }

                @Override
                public void onError(String message) {
                    hideProgressAndShowError(message);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        int displayedChild = mViewAnimator.getDisplayedChild();
        if (displayedChild > 0) {
            mViewAnimator.showPrevious();
        } else {
            super.onBackPressed();
        }
    }

    public void hideProgressAndShowError(String message) {
        mProgressDialog.dismiss();
        if (message == null) {
            message = getString(R.string.error_unkown);
        }
        Utils.showAlertDialog(this, message);
    }
}
