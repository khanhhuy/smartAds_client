package ibeacon.smartadsv1.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ShareActionProvider;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.util.BundleDefined;

public class AdNotifyActitivty extends ActionBarActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_notify);
        Bundle bun = getIntent().getExtras();

        String title = null;

        if (bun != null) {
            url = bun.getString(BundleDefined.URL);
        }
        openByWebView(url);

    }

    private void openByWebView(String url)
    {
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(url);
    }

    private void openByWebBrowser(String url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_ad_notify, menu);

//        MenuItem shareItem = menu.findItem(R.id.action_share);
//        ShareActionProvider mShare = (ShareActionProvider) shareItem.getActionProvider();
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
//
//        mShare.setShareIntent(shareIntent);
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
