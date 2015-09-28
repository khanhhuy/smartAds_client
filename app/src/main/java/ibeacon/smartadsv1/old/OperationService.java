package ibeacon.smartadsv1.old;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.activity.ViewDetailAdsActivity;
import ibeacon.smartadsv1.model.Ad;
import ibeacon.smartadsv1.model.MyBeacon;
import ibeacon.smartadsv1.model.BeaconFilterer;
import ibeacon.smartadsv1.util.BundleDefined;
import ibeacon.smartadsv1.util.Config;
import ibeacon.smartadsv1.util.MessageDefined;

public class OperationService extends Service implements IOperationCallback {

    private BeaconFilterer beaconManager;
    private OperationHandler mOperationHandler;
    private Looper mOperationLooper;
    private HandlerThread mOperationHandlerThread;
    private ServerHandlerThread mServerThread;

    private final class OperationHandler extends Handler {
        public OperationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            Log.d("Thread OP handleMessage", String.format("%d", android.os.Process.myTid()));

            switch (msg.what) {
                case MessageDefined.GET_CUSTOMER_CONTEXTADS:

                    List<MyBeacon> refreshedBeacons = beaconManager.filterBeacons((List<Beacon>) msg.obj);
                    mServerThread.getContextAdsTask(refreshedBeacons);

                    break;
                case MessageDefined.SERVICE_STOP:
                    stopSelf();
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Thread Service onCreate", String.format("%d", android.os.Process.myTid()));
        beaconManager = new BeaconFilterer();
        //Todo: Get list beacon from DB

        mOperationHandlerThread = new HandlerThread("ServiceOperation", Process.THREAD_PRIORITY_BACKGROUND);
        mOperationHandlerThread.start();
        mOperationLooper = mOperationHandlerThread.getLooper();
        mOperationHandler = new OperationHandler(mOperationLooper);

        //Log.d("Thread Service onCreate completed", String.format("%d", android.os.Process.myTid()));

        mServerThread = new ServerHandlerThread(mOperationHandler, this);
        mServerThread.start();
        mServerThread.prepareHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Thread OP onStart", String.format("%d", android.os.Process.myTid()));

        Message msg = mOperationHandler.obtainMessage();
        msg.arg1 = startId;

        String intentType = intent.getExtras().getString(BundleDefined.INTENT_TYPE);

        //create message associated with intent.
        //Send message to handler thread
        switch (intentType) {

            case BundleDefined.INTENT_RECEIVEDBEACONS:
                ArrayList<Parcelable> parcelableArrayList = intent.getExtras().getParcelableArrayList(BundleDefined.LIST_BEACON);
                if (parcelableArrayList.size() == 0) {
                    Log.d("OP create msg", "list beacon = 0");
                    break;
                }

                List<Beacon> beaconReceivedList = new ArrayList<>();
                for (Parcelable parcelable : parcelableArrayList) {
                    beaconReceivedList.add((Beacon) parcelable);
                }

                msg.what = MessageDefined.GET_CUSTOMER_CONTEXTADS;
                msg.obj = beaconReceivedList;

                mOperationHandler.sendMessage(msg);

                break;

            case BundleDefined.INTENT_STOPSERVICE:
                msg.what = MessageDefined.SERVICE_STOP;
                mOperationHandler.sendMessage(msg);
                break;
            default:

                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     *  Implement Callback operation
     */

    @Override
    public void receivedContextAds(List<Ad> contextAdList){
        //Todo: Check if these ads should be displayed to user ?

        //Debug
        GsonBuilder gsonbuilder = new GsonBuilder();
        Gson gson = gsonbuilder.create();
        Log.d("Callback contextAdlist", gson.toJson(contextAdList));

        Bundle bundle = new Bundle();
        String urlPath = Config.HOST + "/ads/" + String.format("%d", contextAdList.get(0).getId());
        bundle.putString(BundleDefined.URL, urlPath);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Received new Ass")
                .setContentText(contextAdList.get(0).getTitle())
                .setDefaults(Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL)
                ;

        Intent notifyIntent = new Intent(this, ViewDetailAdsActivity.class);
        notifyIntent.putExtras(bundle);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notiBuilder.setContentIntent(pendingIntent)
                .setAutoCancel(true);


        Handler uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {

                NotificationCompat.Builder notiBuilder = (NotificationCompat.Builder) inputMessage.obj;
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notiBuilder.build());
            }
        };

        uiHandler.obtainMessage(MessageDefined.START_ACTIVITY, notiBuilder).sendToTarget();

    }

}
