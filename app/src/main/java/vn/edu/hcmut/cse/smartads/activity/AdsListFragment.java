package vn.edu.hcmut.cse.smartads.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vn.edu.hcmut.cse.smartads.R;
import vn.edu.hcmut.cse.smartads.adapter.AdsListRecycleAdapter;
import vn.edu.hcmut.cse.smartads.adapter.SimpleSectionedRecyclerViewAdapter;
import vn.edu.hcmut.cse.smartads.listener.AdsContentListener;
import vn.edu.hcmut.cse.smartads.model.Ads;
import vn.edu.hcmut.cse.smartads.service.ContextAdsService;
import vn.edu.hcmut.cse.smartads.util.BundleDefined;
import vn.edu.hcmut.cse.smartads.util.Config;


public class AdsListFragment extends BaseFragment implements AdsContentListener {

    private SimpleSectionedRecyclerViewAdapter mRecycleSectionedAdapter;
    private AdsListRecycleAdapter mAdsListAdapter;
    private RecyclerView mRecyclerView;
    private Activity mActivity;

    private List<Ads> mlistAds;
    private List<SimpleSectionedRecyclerViewAdapter.Section> sectionList;

    public AdsListFragment() {
        // Required empty public constructor
    }

    public void setup(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!loadData()) {
            return inflater.inflate(R.layout.fragment_ads_empty, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_ads_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.listAds);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdsListAdapter = new AdsListRecycleAdapter(R.layout.card_ads_item, mActivity, mlistAds);

        SimpleSectionedRecyclerViewAdapter.Section[] sections = new SimpleSectionedRecyclerViewAdapter.Section[sectionList.size()];
        mRecycleSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(mActivity, R.layout.card_section, R.id.card_section_text, mAdsListAdapter);
        mRecycleSectionedAdapter.setSections(sectionList.toArray(sections));

        mAdsListAdapter.setOnItemClickListener(new AdsListRecycleAdapter.OnAdsClickListener() {
            @Override
            public void onAdsClick(View view, int position) {
                if (position != RecyclerView.NO_POSITION) {

                    Bundle bundle = new Bundle();
                    String urlPath = Config.HOST_BASE + "/ads/" + String.valueOf(mlistAds.get(position).getAdsId());
                    bundle.putString(BundleDefined.URL, urlPath);
                    bundle.putString(BundleDefined.ADS_ID, String.valueOf(mlistAds.get(position).getAdsId()));
                    bundle.putInt(BundleDefined.ADS_ADAPTER_POSITION, position);

                    Intent detailAdsIntent = new Intent(mActivity, ViewDetailAdsActivity.class);
                    detailAdsIntent.putExtras(bundle);
                    startActivityForResult(detailAdsIntent, MainActivity.VIEW_DETAILS_ADS, bundle);

                }
            }
        });

        mRecyclerView.setAdapter(mRecycleSectionedAdapter);

        NotificationManager notificationManager = ContextAdsService.getNotificationManager();
        if (notificationManager != null)
            notificationManager.cancelAll();

        return  view;
    }

    private boolean loadData() {
        Iterator allAds = Ads.findAll(Ads.class);

        if (!allAds.hasNext())
            return false;

        List<Ads> others = new ArrayList<>();
        List<Ads> expired = new ArrayList<>();
        mlistAds = new ArrayList<>();

        while (allAds.hasNext()) {
            Ads ads = (Ads)allAds.next();
            Log.d(Config.TAG, "Last received ads " + ads.getAdsId() + " " + ads.getLastReceived());
            if (!ads.is_blacklisted()) {
                if (ads.getLastReceived() != null)
                    if ((new DateTime()).minusHours(Config.JUST_RECEIVED_TIME_HOUR).compareTo(ads.getLastReceived()) < 0) {
                        mlistAds.add(ads);
                        continue;
                    }
                if (ads.getEndDate() != null)
                    if ((new DateTime()).compareTo(ads.getEndDate()) > 0) {
                        expired.add(ads);
                        continue;
                    }
                others.add(ads);
            }
        }

        createSectionHeader(mlistAds.size(), others.size());
        mlistAds.addAll(others);
        mlistAds.addAll(expired);

        return true;
    }

    private void createSectionHeader(int justReceivedSection, int othersSection) {
        sectionList = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
        sectionList.add(new SimpleSectionedRecyclerViewAdapter.Section(0,
                getResources().getString(R.string.just_received_section)));
        sectionList.add(new SimpleSectionedRecyclerViewAdapter.Section(justReceivedSection,
                getResources().getString(R.string.others_section)));
        sectionList.add(new SimpleSectionedRecyclerViewAdapter.Section(justReceivedSection + othersSection,
                getResources().getString(R.string.expired_section)));
    }

    private void reloadDataset() {
        loadData();
        SimpleSectionedRecyclerViewAdapter.Section[] sections = new SimpleSectionedRecyclerViewAdapter.Section[sectionList.size()];
        mRecycleSectionedAdapter.setSections(sectionList.toArray(sections));
        mAdsListAdapter.setAdsData(mlistAds);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.VIEW_DETAILS_ADS && resultCode == MainActivity.RESULT_OK) {
            Bundle bun = data.getExtras();
            String adsBlacklist = bun.getString(BundleDefined.ADS_BLACKLIST_ID, "");
            Log.d(Config.TAG, "Ads blacklist " + adsBlacklist);
            if (!adsBlacklist.isEmpty() && mAdsListAdapter != null) {
                int position = bun.getInt(BundleDefined.ADS_ADAPTER_POSITION, -1);
                Log.d(Config.TAG, "Remove position " + position);
                if (position != -1) {
                    reloadDataset();
                    mRecycleSectionedAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void adsListChange(List<Ads> newAds) {
        Log.d(Config.TAG, "AdsList Change");
        mlistAds.clear();
        mRecycleSectionedAdapter.notifyItemRangeRemoved(0, mlistAds.size());
        mlistAds.addAll(newAds);
        mRecycleSectionedAdapter.notifyItemRangeInserted(0, mlistAds.size() - 1);
    }

    @Override
    public void adsListUpdateImg(int position) {
        Log.d(Config.TAG, "Ads update position " + position);
        mRecycleSectionedAdapter.notifyItemChanged(position + 1);
    }
}
