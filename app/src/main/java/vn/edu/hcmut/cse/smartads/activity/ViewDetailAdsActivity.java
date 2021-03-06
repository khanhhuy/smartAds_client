package vn.edu.hcmut.cse.smartads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.connector.Connector;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;
import vn.edu.hcmut.cse.smartads.util.Utils;

public class ViewDetailAdsActivity extends AppCompatActivity implements FeedBackFragment.NoticeDialogListener {

    private String url;
    private String adsId;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_ads);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bun = getIntent().getExtras();

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        //mWebView.getSettings().setJavaScriptEnabled(true);

        loadAds(bun);
    }

    private void loadAds(Bundle bun) {
        if (bun != null) {
            if (!Utils.isNetworkConnected(this)) {
                Utils.showAlertDialog(this, getString(R.string.no_internet), getString(R.string.enable_internet_to_view_ads));
            }
            url = bun.getString(BundleDefined.URL);
            adsId = bun.getString(BundleDefined.ADS_ID);
            List<Ads> listAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
            if (!listAds.isEmpty()) {
                Log.d(Config.TAG, "Is viewed" + listAds.get(0).is_viewed());
                listAds.get(0).setViewed(true);
                listAds.get(0).insertOrUpdate();
            }
            mWebView.loadUrl(url);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Ads ads = Ads.findAds(adsId);
        if (ads != null) {
            if (!ads.getType().equals(Ads.TARGETED_ADS))
                inflater.inflate(R.menu.menu_ads_detail, menu);
            else
                inflater.inflate(R.menu.menu_ads_details_nofeedback, menu);
        }
        else
            inflater.inflate(R.menu.menu_ads_details_nofeedback, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_feedback:
                showFeedbackDialog();
                return true;
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (supportShouldUpRecreateTask(upIntent) || isTaskRoot()) {
                    Log.d(Config.TAG, "Up from launcher");
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    Log.d(Config.TAG, "Up from MainActivity");
                    supportNavigateUpTo(upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFeedbackDialog() {

        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.no_internet_feedback), Toast.LENGTH_SHORT).show();
            return;
        }

        DialogFragment feedback = FeedBackFragment.newInstance();
        feedback.show(getSupportFragmentManager(), Config.TAG);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        List<Ads> listAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
        if (!listAds.isEmpty()) {
            listAds.get(0).setBlacklisted(true);
            listAds.get(0).insertOrUpdate();
        }
        Connector.getInstance(this).sendFeedback(adsId);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    protected void onDestroy() {
        Log.d(Config.TAG, "ViewDetailAdsActivity Destroyed");
        super.onDestroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadAds(intent.getExtras());
        invalidateOptionsMenu();
    }
}
