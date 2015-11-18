package vn.edu.hcmut.cse.smartads.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.connector.LoginResponseListener;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.model.Minor;
import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.service.RemoteSettingService;
import vn.edu.hcmut.cse.smartads.settings.SettingServiceRequestType;
import vn.edu.hcmut.cse.smartads.settings.dev.DevConfigActivity;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.GeofenceManager;
import vn.edu.hcmut.cse.smartads.util.Utils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginResponseListener {

    public static final String AUTH_PREFS_NAME = "AuthPrefsFile";
    public static final String CUSTOMER_ID = "customerID";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String LOGGED_ID = "loggedIn";

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Connector mConnector;
    private ProgressDialog mProgressDialog;

    private BluetoothManager bluetoothManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView textSignUp = (TextView) findViewById(R.id.text_signup);
        textSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });
        mProgressDialog = Utils.createLoadingDialog(this);

        //dev config secret
        TextView logo = (TextView) findViewById(R.id.smartads_logo);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(LoginActivity.this, DevConfigActivity.class);
                startActivity(i);
                return true;
            }
        });
    }

    private void openSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if (mConnector == null) {
                mConnector = Connector.getInstance(LoginActivity.this);
            }
            mConnector.postLogin(email, password, LoginActivity.this);
        }
    }

    private boolean isEmailValid(String email) {
        return Utils.isValidEmail(email);
    }

    public void showProgress(final boolean show) {
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }*/
    }

    @Override
    public void onSuccess(String customerID, String accessToken) {
        showProgress(false);

        String oldCustomerID = Utils.getCustomerID(this);
        if (!customerID.equals(oldCustomerID)) {
            Ads.deleteAll(Ads.class);
            Minor.deleteAll(Minor.class);
            clearDefaultSharedPrefs();
        }
        login(this, customerID, accessToken);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        mConnector.updateRequest();

        Intent restoreSettingIntent = new Intent(this, RemoteSettingService.class);
        restoreSettingIntent.putExtra(RemoteSettingService.SERVICE_REQUEST_TYPE, SettingServiceRequestType.RESTORE_FROM_SERVER);
        startService(restoreSettingIntent);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            startService(new Intent(this, ContextAdsService.class));
        }
        else {
            GeofenceManager.getInstance(this).startGeofencing();
            Log.d(Config.TAG, "Start Geofence at Login");
        }

        finish();
    }

    private void clearDefaultSharedPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();
    }


    @Override
    public void onError(String message) {
        showProgress(false);
        if (message == null) {
            message = getString(R.string.error_unkown);
        }
        mPasswordView.requestFocus();
        Utils.showAlertDialog(this, message);
    }

    private static void login(Context context, String customerID, String accessToken) {
        SharedPreferences authPrefs = context.getSharedPreferences(AUTH_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = authPrefs.edit();

        editor.putBoolean(LOGGED_ID, true);
        editor.putString(CUSTOMER_ID, customerID);
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static void logout(Context context) {
        SharedPreferences authPrefs = context.getSharedPreferences(AUTH_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = authPrefs.edit();

        editor.remove(LOGGED_ID);
        editor.apply();

        context.stopService(new Intent(context, ContextAdsService.class));
    }



}

