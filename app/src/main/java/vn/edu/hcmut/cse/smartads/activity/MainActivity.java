package vn.edu.hcmut.cse.smartads.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.old.OperationService;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerCallback {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLoggedIn();

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_adslist_header);
        setSupportActionBar(mToolbar);

        setTitle("Featured Ads");


//        Set up the drawer.
//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getFragmentManager().findFragmentById(R.id.fragment_drawer);
//        mNavigationDrawerFragment.setup(R.id.fragment_drawer,
//                (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        initFragment();

        //Intent beaconServiceIntent = new Intent(this, ContextAdsService.class);
        //startService(beaconServiceIntent);
        //testOPservice();

    }

    private void checkLoggedIn() {
        SharedPreferences authPrefs=getSharedPreferences(LoginActivity.AUTH_PREFS_NAME,MODE_PRIVATE);
        boolean loggedIn=authPrefs.getBoolean("loggedIn",false);
        if (!loggedIn){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        stopService(intentOP);
        super.onDestroy();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    /**
     * Debug & testing offline
     */
    private void testOPservice() {

        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        Bundle bundle = new Bundle();
        ArrayList<Beacon> beaconArrayList = new ArrayList<>();
        Beacon beacon = new Beacon("12345678123456781234567812345678", "dummy1", "0x24", 1, 2, 1, 1);
        beaconArrayList.add(beacon);
        bundle.putParcelableArrayList(BundleDefined.LIST_BEACON, beaconArrayList);
        bundle.putString(BundleDefined.INTENT_TYPE, BundleDefined.INTENT_RECEIVEDBEACONS);
        intentOP.putExtras(bundle);

        startService(intentOP);

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

}