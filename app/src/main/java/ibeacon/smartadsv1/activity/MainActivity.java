package ibeacon.smartadsv1.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.service.OperationService;
import ibeacon.smartadsv1.util.BundleDefined;


public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Thread MainActivity", String.format("%d", android.os.Process.myTid()));

        Button button = (Button) findViewById(R.id.btnAddText);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView moreText = new TextView(getApplicationContext());
                moreText.setText("Hello World");
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view);
                linearLayout.addView(moreText);
            }
        });

        //testOPservice();
    }

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
    public void onDestroy() {
        Intent intentOP = new Intent(getApplicationContext(), OperationService.class);
        stopService(intentOP);
        super.onDestroy();
    }
}
