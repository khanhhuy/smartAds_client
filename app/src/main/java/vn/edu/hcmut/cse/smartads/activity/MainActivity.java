package vn.edu.hcmut.cse.smartads.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.listener.MyBeacon;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.model.Minor;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerCallback {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    public static final int VIEW_DETAILS_ADS = 5;
    public static final int RESULT_OK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        setContentView(R.layout.activity_main);

//        mToolbar = (Toolbar) findViewById(R.id.toolbar_adslist_header);
//        setSupportActionBar(mToolbar);

        setTitle("Featured Ads");

        checkLoggedIn();
        updateRequest();
        initFragment();

//        Set up the drawer.
//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getFragmentManager().findFragmentById(R.id.fragment_drawer);
//        mNavigationDrawerFragment.setup(R.id.fragment_drawer,
//                (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        //Intent beaconServiceIntent = new Intent(this, ContextAdsService.class);
        //startService(beaconServiceIntent);

    }

    private void checkLoggedIn() {
        SharedPreferences authPrefs = getSharedPreferences(LoginActivity.AUTH_PREFS_NAME, MODE_PRIVATE);
        boolean loggedIn=authPrefs.getBoolean("loggedIn", false);
        if (!loggedIn){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateRequest() {
        SharedPreferences timePrefs = getSharedPreferences(Connector.PREF_TIME, MODE_PRIVATE);
        String timeStr = timePrefs.getString(Connector.UPDATE_REQUEST_DATE, "");
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Config.DATETIME_PATTERN);
        if (!timeStr.isEmpty()) {
            DateTime lastUpdateReq = DateTime.parse(timeStr, formatter);
            if ((new DateTime()).minusDays(Config.SERVER_UPDATE_REQUEST_MIN_DATE)
                    .compareTo(lastUpdateReq) > 0) {
                Connector.getInstance(this).updateRequest();
            }
        } else {
            SharedPreferences.Editor editor = timePrefs.edit();
            editor.putString(Connector.UPDATE_REQUEST_DATE, formatter.print(new DateTime()));
            editor.commit();
            Connector.getInstance(this).updateRequest();
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
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
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
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Config.TAG, "onActivityResult Main call");
    }

    private void initFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AdsListFragment adsListFragment = new AdsListFragment();
        adsListFragment.setup(mToolbar);

        fragmentTransaction.add(R.id.inner_container, adsListFragment);

        setTitle("Featured Ads");
        fragmentTransaction.commit();

    }


    /**
     * Debug & testing
     */

    private void testConnector() {

        Connector connector = Connector.getInstance(this);
        Beacon beacon = new Beacon("12345678123456781234567812345678", "dummy1", "0x24", 1, 2, 1, 1);
        MyBeacon myBeacon = new MyBeacon(beacon);
        List<MyBeacon> beaconList = new ArrayList<>();
        beaconList.add(myBeacon);
        connector.requestContextAds(beaconList, null);
    }

}
