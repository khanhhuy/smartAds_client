package vn.edu.hcmut.cse.smartads.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;


import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.settings.dev.DevConfigActivity;
import vn.edu.hcmut.cse.smartads.util.Config;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private AdsListFragment mAdsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.header_ads_list));


        checkLoggedIn();


        ContextAdsService.checkBluetoothAndStart(this);
    }

    private void checkLoggedIn() {
        SharedPreferences authPrefs = getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, MODE_PRIVATE);
        boolean loggedIn = authPrefs.getBoolean("loggedIn", false);
        if (!loggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            initFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(this).setTitle(R.string.log_out_confirm_title).setMessage(R.string.logout_message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.logout(MainActivity.this);

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                }
            }).setNegativeButton(R.string.cancel, null).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Config.TAG, "MainActivity On Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Config.TAG, "MainActivity On Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mAdsListFragment = new AdsListFragment();
        mAdsListFragment.setup(mToolbar);

        fragmentTransaction.add(R.id.inner_container, mAdsListFragment);

        fragmentTransaction.commit();

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, DevConfigActivity.class);
            startActivity(i);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdsListFragment.onRestart();
    }

}
