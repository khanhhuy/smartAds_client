package ibeacon.smartadsv1.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;

import com.estimote.sdk.Beacon;

import ibeacon.smartadsv1.MyBeaconManager;

public class OperationService extends Service {

    private MyBeaconManager beaconManager;
    private OperationHandler mOperationHandler;


    private final class OperationHandler extends Handler {
        public OperationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            //Todo: parse different type of messages here

            //raise a new notification

            //destroy service when last message is processed
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        beaconManager = new MyBeaconManager();

        HandlerThread handlerThread = new HandlerThread("ServiceOperation", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mOperationHandler = new OperationHandler(handlerThread.getLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //Currently just create a new beacon

        String uuid = intent.getStringExtra("uuid");

        //create dummy beacon with UUID for testing. In future, need to use Parcelable<Beacon>.

        Beacon beacon = new Beacon(uuid, "mybeacon", null, 1, 1, 100, 10);
        if (beaconManager.isNewBeacon(beacon))
        {
            //Todo: raise a new notification
        }

        //send new message to the queue
        Message msg = mOperationHandler.obtainMessage();
        msg.arg1 = startId;
        msg.arg2 = beacon.getMinor();
        msg.sendToTarget();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
