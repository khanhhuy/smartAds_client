package ibeacon.smartadsv1.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
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

import ibeacon.smartadsv1.R;


public class MainActivity extends ActionBarActivity {


    private BroadcastReceiver beaconReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast", "beacon received");
            String uuid = intent.getStringExtra("uuid");

            long endTime = System.currentTimeMillis() + 8*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

            Log.d("System", "Resumed");
            //String threadID = new Long(Thread.currentThread().getId()).toString();
            String threadID = new Integer(android.os.Process.myTid()).toString();
            Log.d("Thread onReceive", threadID);
            Toast.makeText(getApplicationContext(), uuid, Toast.LENGTH_SHORT).show();
        }
    };

    private BroadcastReceiver regionExited = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast", "beacon exited");
            Toast.makeText(getApplicationContext(), "Region exited", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(beaconReceiver, new IntentFilter("beaconReceived"));
        LocalBroadcastManager.getInstance(this).registerReceiver(regionExited, new IntentFilter("regionExited"));

        //String threadID = new Long(Thread.currentThread().getId()).toString();
        String threadID = new Integer(android.os.Process.myTid()).toString();
        Log.d("Thread Activity", threadID);

        Button button = (Button) findViewById(R.id.btnAddText);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView moreText = new TextView(getApplicationContext());
                moreText.setText("Hello World");
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view);
                linearLayout.addView(moreText);
            }
        });
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
}
