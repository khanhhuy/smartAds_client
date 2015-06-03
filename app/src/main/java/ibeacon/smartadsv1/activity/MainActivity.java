package ibeacon.smartadsv1.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.service.ContextAdsService;
import ibeacon.smartadsv1.old.OperationService;
import ibeacon.smartadsv1.util.BundleDefined;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerCallback {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        //Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer,
                (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        Intent beaconServiceIntent = new Intent(this, ContextAdsService.class);
        startService(beaconServiceIntent);

        //testOPservice();
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

}
