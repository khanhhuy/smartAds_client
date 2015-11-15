package vn.edu.hcmut.cse.smartads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    private int adsAdapterPostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_ads);
        Bundle bun = getIntent().getExtras();

        if (bun != null) {
            url = bun.getString(BundleDefined.URL);
            adsId = bun.getString(BundleDefined.ADS_ID);
            adsAdapterPostion = bun.getInt(BundleDefined.ADS_ADAPTER_POSITION, -1);
            List<Ads> listAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
            if (!listAds.isEmpty()) {
                Log.d(Config.TAG, "Is viewed" + listAds.get(0).is_viewed());
                listAds.get(0).setViewed(true);
                listAds.get(0).InsertOrUpdate();
            }
        }
        if (!Utils.isNetworkConnected(this)) {
            Utils.showAlertDialog(this, getString(R.string.no_internet), getString(R.string.enable_internet_to_view_ads));
        }

        openByWebView(url);

    }

    private void openByWebView(String url)
    {
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
//        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ads_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.action_feedback:
                showFeedbackDialog();
                return true;
            case android.R.id.home:
                Log.d(Config.TAG, "ViewDetail Up nav pressed");
                Bundle bundle = new Bundle();
                bundle.putInt(BundleDefined.ADS_ADAPTER_POSITION, adsAdapterPostion);
                finishWithResult(bundle, MainActivity.RESULT_VIEWED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(Config.TAG, "ViewDetail Back pressed");
        super.onBackPressed();
    }

    private void finishWithResult(Bundle bundle, int result) {
        Intent data = new Intent();
        data.putExtras(bundle);
        if (getParent() == null) {
            setResult(result, data);
        }
        else
            getParent().setResult(result, data);
        finish();
    }

    private void showFeedbackDialog() {
        DialogFragment feedback = FeedBackFragment.newInstance();
        feedback.show(getSupportFragmentManager(), Config.TAG);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        List<Ads> listAds = new ArrayList<>(Ads.find(Ads.class, "ads_id = ?", adsId));
        if (!listAds.isEmpty()) {
            listAds.get(0).setBlacklisted(true);
            listAds.get(0).InsertOrUpdate();
        }
        Bundle bundle = new Bundle();
        bundle.putString(BundleDefined.ADS_BLACKLIST_ID, adsId);
        bundle.putInt(BundleDefined.ADS_ADAPTER_POSITION, adsAdapterPostion);
        Connector.getInstance(this).sendFeedback(adsId);
        finishWithResult(bundle, MainActivity.RESULT_DELETED);
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
}
