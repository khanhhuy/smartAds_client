package ibeacon.smartadsv1.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ibeacon.smartadsv1.R;
import ibeacon.smartadsv1.adapter.AdListRecycleAdapter;
import ibeacon.smartadsv1.connector.Connector;
import ibeacon.smartadsv1.listener.AdsContentListener;
import ibeacon.smartadsv1.model.Ad;
import ibeacon.smartadsv1.listener.HidingScrollListener;
import ibeacon.smartadsv1.util.BundleDefined;
import ibeacon.smartadsv1.util.Config;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the @link AdsListFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class AdsListFragment extends BaseFragment implements AdsContentListener {

    private OnFragmentInteractionListener mListener;
    private AdListRecycleAdapter mRecycleAdapter;
    private RecyclerView mRecyclerView;
    private Connector mConnector;

    private List<Ad> mlistAd;
    private Activity mActivity;

    public AdsListFragment() {
        // Required empty public constructor
        mlistAd = new ArrayList<>();
        //createDummyAdsList();
    }

    public void setup(Toolbar toolbar) {
        mToolbar = toolbar;

    }

    private void createDummyAdsList() {
        mlistAd.add(new Ad(1, "Pepsi discount", "15%", new Date(), new Date()));
        mlistAd.add(new Ad(2, "Unilever discount", "All products for 30%", new Date(), new Date()));
        mlistAd.add(new Ad(3, "Unilever discount",
                "All products for 30%, this text will got 2 lines I hope that", new Date(), new Date()));
        mlistAd.add(new Ad(4, "Unilever discount", "All products for 30%", new Date(), new Date()));
        mlistAd.add(new Ad(1, "Pepsi discount", "15%", new Date(), new Date()));
        mlistAd.add(new Ad(2, "Unilever discount", "All products for 30%", new Date(), new Date()));
        mlistAd.add(new Ad(3, "Unilever discount",
                "All products for 30%, this text will got 2 lines I hope that", new Date(), new Date()));
        mlistAd.add(new Ad(4, "Unilever discount", "All products for 30%", new Date(), new Date()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mConnector = Connector.getInstance(mActivity);

        View view = inflater.inflate(R.layout.fragment_ads_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.listAds);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        mRecycleAdapter = new AdListRecycleAdapter(mlistAd, R.layout.card_ads_item, R.layout.header_ads_item);
        mRecycleAdapter.setOnitemClickListener(new AdListRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != RecyclerView.NO_POSITION) {

                    Bundle bundle = new Bundle();
                    String urlPath = Config.HOST + "/ads/" + String.format("%d", mlistAd.get(position).getId());
                    bundle.putString(BundleDefined.URL, urlPath);

                    Intent detailAdIntent = new Intent(mActivity, AdNotifyActitivty.class);
                    detailAdIntent.putExtras(bundle);
                    startActivity(detailAdIntent);

                }
            }
        });

        mRecyclerView.setAdapter(mRecycleAdapter);

        mConnector.requestAllAds(this);

        return  view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void adsListChange(List<Ad> newAds) {
        Log.d(Config.TAG, "AdsList Change");
        mlistAd.clear();
        mRecycleAdapter.notifyItemRangeRemoved(0, mlistAd.size());
        mlistAd.addAll(newAds);
        mRecycleAdapter.notifyItemRangeInserted(0, mlistAd.size() - 1);
        mConnector.requestImageThumbnail(mlistAd, this);
    }

    @Override
    public void adsListUpdateImg(int position) {
        Log.d(Config.TAG, "Ads update position " + position);
        mRecycleAdapter.notifyItemChanged(position + 1);
    }
}
